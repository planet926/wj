package com.jvm;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Exception in jvm "main" java.util.ConcurrentModificationException
 * Test
 */
public class ConcurrentModificationExceptionTest {

    private long count;

    /**
     * 单线程重现方式
     */
    public void test1()  {

        ArrayList<Integer> arrayList = collectionInit();

        // 复现方法一
        Iterator<Integer> iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            Integer integer = iterator.next();
            if (integer.intValue() == 5) {
                arrayList.remove(integer);
            }
        }

        // 复现方法二
        iterator = arrayList.iterator();
        for (Integer value : arrayList) {
            Integer integer = iterator.next();
            if (integer.intValue() == 5) {
                arrayList.remove(integer);
            }
        }
    }

    public ArrayList collectionInit() {
        ArrayList<Integer> arrayList = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            arrayList.add(Integer.valueOf(i));
        }

        return arrayList;
    }


}
