package net.windia.insdata.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Tester {

    @Test
    public void test() {
        List<A> listOfA = new ArrayList<>();
        listOfA.add(new A());
        listOfA.add(new A());

        List<B> listOfB = new ArrayList<>();
        listOfB.add(new B());
        listOfB.add(new B());
        listOfB.add(new B());

        List<List<? extends E>> list = new ArrayList<>();
        list.add(listOfA);
        list.add(listOfB);

        System.out.println(list);
    }
}

class E {
    void print() {
        System.out.println("E");
    }
}

class A extends E {
    void print() {
        System.out.println("A");
    }
}

class B extends E {
    void print() {
        System.out.println("B");
    }
}
