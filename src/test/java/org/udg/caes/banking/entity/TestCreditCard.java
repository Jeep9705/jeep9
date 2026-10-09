package org.udg.caes.banking.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCreditCard {

    @Test
    void testCredit(){
        CreditCard cc = new CreditCard("test");
        cc.credit(100);

        assertEquals(100, cc.getCredit());
    }

    @Test
    void testReset(){
        CreditCard cc = new CreditCard("test");

        cc.credit(100);
        cc.reset();

        assertEquals(0, cc.getCredit());
    }

    @Test
    void testGetId(){
        CreditCard cc = new CreditCard("test");

        assertEquals("test", cc.getId());
    }

    @Test
    void testGetMaxCredit(){
        CreditCard cc = new CreditCard("test");

        assertEquals(0, cc.getMaxCredit());
    }
    @Test
    void testSetActive(){
        CreditCard cc = new CreditCard("test");

        cc.setActive(true);
    }
}