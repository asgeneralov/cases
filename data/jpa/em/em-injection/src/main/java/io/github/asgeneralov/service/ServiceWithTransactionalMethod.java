package io.github.asgeneralov.service;

import io.github.asgeneralov.data.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceWithTransactionalMethod {

    @PersistenceContext
    private EntityManager emPc;

    @Autowired
    private EntityManager emAw;

    public static final String SUFFIX = "_modified";

    public void printEmClasses() {
        System.out.println("Em persistence context :"  + emPc.toString());
        System.out.println("Em autowired :"  + emAw.toString());
    }

    @Transactional
    public void methodTransactionalEmPersistenceContext(Long id) {
        Person p = emPc.find(Person.class, id);
        p.setName(p.getName() + SUFFIX);
    }

    @Transactional
    public void methodTransactionalEmAutowired(Long id) {
        Person p = emAw.find(Person.class, id);
        p.setName(p.getName() + SUFFIX);
    }

    public void methodNonTransactionalEmPersistenceContext(Long id) {
        Person p =  emAw.find(Person.class, id);
        p.setId(id);
        p.setName(p.getName() + SUFFIX);
        emPc.merge(p);
    }

    public void methodNonTransactionalEmAutowired(Long id) {
        Person p =  emAw.find(Person.class, id);
        p.setId(id);
        p.setName(p.getName() + SUFFIX);
        emAw.merge(p);
    }


}
