package com.shelly.dogs;


import org.junit.Test;

import static org.junit.Assert.*;

public class SizeTest {


    @Test
    public void testDimensions() {
        Size size = new Size(10, 20);
        assertEquals(size.getWidth(), 10);
        assertEquals(size.getHeight(), 20);
        assertEquals("10x20", size.toString());
    }

    @Test
    public void testFlip() {
        Size size = new Size(10, 20);
        Size flipped = size.flip();
        assertEquals(size.getWidth(), flipped.getHeight());
        assertEquals(size.getHeight(), flipped.getWidth());
    }

    @Test
    public void testEquals() {
        Size s1 = new Size(10, 20);
        assertTrue(s1.equals(s1));
        assertFalse(s1.equals(null));
        assertFalse(s1.equals(""));

        Size s2 = new Size(10, 0);
        Size s3 = new Size(10, 20);
        assertTrue(s1.equals(s3));
        assertFalse(s1.equals(s2));
    }

    @Test
    public void testHashCode() {
        Size s1 = new Size(10, 20);
        Size s2 = new Size(10, 0);
        assertNotEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    public void testCompare() {
        Size s1 = new Size(10, 20);
        Size s2 = new Size(10, 0);
        Size s3 = new Size(10, 20);
        assertTrue(s1.compareTo(s3) == 0);
        assertTrue(s1.compareTo(s2) > 0);
        assertTrue(s2.compareTo(s1) < 0);
    }
}
