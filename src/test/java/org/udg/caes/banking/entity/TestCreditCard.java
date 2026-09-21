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
}
