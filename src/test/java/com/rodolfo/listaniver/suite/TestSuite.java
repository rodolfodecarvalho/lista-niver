package com.rodolfo.listaniver.suite;

import com.rodolfo.listaniver.controller.EmailControllerTest;
import com.rodolfo.listaniver.controller.PessoaControllerTest;
import com.rodolfo.listaniver.integration.EmailIntegrationTest;
import com.rodolfo.listaniver.integration.PessoaIntegrationTest;
import com.rodolfo.listaniver.repository.EmailRepositoryTest;
import com.rodolfo.listaniver.repository.PessoaRepositoryTest;
import com.rodolfo.listaniver.service.EmailServiceTest;
import com.rodolfo.listaniver.service.PessoaServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Suite de Testes")
@SelectClasses({
        PessoaControllerTest.class,
        PessoaServiceTest.class,
        PessoaIntegrationTest.class,
        EmailControllerTest.class,
        EmailServiceTest.class,
        EmailIntegrationTest.class,
        EmailRepositoryTest.class,
        PessoaRepositoryTest.class
})
public class TestSuite {
}