package com.rodolfo.listaniver.suite;

import com.rodolfo.listaniver.controller.PessoaControllerTest;
import com.rodolfo.listaniver.integration.PessoaIntegrationTest;
import com.rodolfo.listaniver.service.PessoaServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Suite de Testes - Sistema de Pessoas")
@SelectClasses({
        PessoaControllerTest.class,
        PessoaServiceTest.class,
        PessoaIntegrationTest.class
})
public class PessoaTestSuite {
}