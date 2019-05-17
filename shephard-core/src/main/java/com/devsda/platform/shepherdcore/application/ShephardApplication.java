package com.devsda.platform.shepherdcore.application;

import com.devsda.platform.shepherdcore.constants.ShephardConstants;
import com.devsda.platform.shepherdcore.dao.RegisterationDao;
import com.devsda.platform.shepherdcore.dao.WorkflowOperationDao;
import com.devsda.platform.shepherdcore.model.ShephardConfiguration;
import com.devsda.platform.shepherdcore.resources.*;
import com.devsda.platform.shepherdcore.service.ExecuteWorkflowRunner;
import com.devsda.platform.shepherdcore.service.ExecuteWorkflowServiceHelper;
import com.devsda.platform.shepherdcore.service.NodeExecutor;
import com.devsda.platform.shepherdcore.util.RequestValidator;
import com.devsda.platform.shepherdcore.resources.*;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.skife.jdbi.v2.DBI;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;


/**
 * This is a Shepherd Application Context class. Entry point of Shepherd-core.
 */
public class ShephardApplication extends Application<ShephardConfiguration> {

    public static void main(String[] args) throws Exception {
        new ShephardApplication().run(args);
    }

    /**
     * This is a main run method to start Shepherd application
     *
     * @param shephardConfiguration
     * @param environment
     * @throws Exception
     */
    public void run(ShephardConfiguration shephardConfiguration, Environment environment) throws Exception {

        Injector injector = createInjector(shephardConfiguration, environment);

        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter(ShephardConstants.ServletFilter.CORS, CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter(ShephardConstants.ServletFilter.ALLOWED_ORIGINS, ShephardConstants.ServletFilter.ALLOWED_ORIGIN_NAMES);
        cors.setInitParameter(ShephardConstants.ServletFilter.ALLOWED_HEADERS, ShephardConstants.ServletFilter.ALLOWED_HEADER_NAMES);
        cors.setInitParameter(ShephardConstants.ServletFilter.ALLOWED_METHODS, ShephardConstants.ServletFilter.ALLOWED_METHOD_NAMES);

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        environment.jersey().register(HealthCheckReources.class);
        environment.jersey().register(injector.getInstance(ClientRegisterationResources.class));
        environment.jersey().register(injector.getInstance(ClientDataRetrievalResources.class));
        environment.jersey().register(injector.getInstance(ExecuteWorkflowResources.class));
        environment.jersey().register(injector.getInstance(ClientUpdateInformationResources.class));
        environment.jersey().register(injector.getInstance(WorkflowManagementResources.class));
    }

    /**
     * This method creates a injector
     *
     * @param shephardConfiguration
     * @param environment
     * @return
     */
    public Injector createInjector(ShephardConfiguration shephardConfiguration, Environment environment) {

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {

                // Binding DB layer with Application layer.
                final DBIFactory factory = new DBIFactory();
                final DBI jdbi = factory.build(environment, shephardConfiguration.getDatabase(), ShephardConstants.DB.MYSQL);
                final RegisterationDao registerationDao = jdbi.onDemand(RegisterationDao.class);
                final WorkflowOperationDao workflowOperationDao = jdbi.onDemand(WorkflowOperationDao.class);

                bind(RegisterationDao.class).toInstance(registerationDao);
                bind(WorkflowOperationDao.class).toInstance(workflowOperationDao);

                requestStaticInjection(ExecuteWorkflowRunner.class);
                requestStaticInjection(ExecuteWorkflowServiceHelper.class);
                requestStaticInjection(NodeExecutor.class);
                requestStaticInjection(RequestValidator.class);

                // Other objects
                bind(ShephardConfiguration.class).toInstance(shephardConfiguration);
            }
        });

        return injector;
    }
}
