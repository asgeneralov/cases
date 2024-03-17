package io.github.asgeneralov;

import io.github.asgeneralov.data.Person;
import io.github.asgeneralov.jdbc.PersonRowMapper;
import io.github.asgeneralov.service.ServiceWithTransactionalMethod;
import jakarta.persistence.TransactionRequiredException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

import static io.github.asgeneralov.service.ServiceWithTransactionalMethod.SUFFIX;

@SpringBootTest(classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceWithTransactionalMethodTest {

    @Autowired
    private ServiceWithTransactionalMethod service;

    @Autowired
    private DataSource dataSource;

    @BeforeAll
    public void prepare() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("insert into person (id, name) values (1, 'Vasya'), (2, 'Petya'), (3, 'Masha'), (4, 'Nina')");
    }

    @Test
    public void printClasses() {
        service.printEmClasses();
    }

    @Test
    public void testMethodTransactionalEmPc() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        service.methodTransactionalEmPersistenceContext(2L);
        Person person = jdbcTemplate.queryForObject("select id, name from person where id = 2", new PersonRowMapper());
        Assertions.assertEquals("Petya" + SUFFIX, person.getName());
    }

    @Test
    public void testMethodTransactionalEmAw() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        service.methodTransactionalEmAutowired(1L);
        Person person = jdbcTemplate.queryForObject("select id, name from person where id = 1", new PersonRowMapper());
        Assertions.assertNotEquals("Vasya" + SUFFIX, person.getName());
    }

    @Test
    public void testMethodNonTransactionalEmPc() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Assertions.assertThrowsExactly(TransactionRequiredException.class, () -> {
            service.methodNonTransactionalEmPersistenceContext(3L);
        });
        Person person = jdbcTemplate.queryForObject("select id, name from person where id = 3", new PersonRowMapper());
        Assertions.assertNotEquals("Masha" + SUFFIX, person.getName());
    }

    @Test
    public void testMethodNonTransactionalEmAw() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        service.methodNonTransactionalEmAutowired(4L);
        Person person = jdbcTemplate.queryForObject("select id, name from person where id = 4", new PersonRowMapper());
        Assertions.assertNotEquals("Nina" + SUFFIX, person.getName());
    }

}

