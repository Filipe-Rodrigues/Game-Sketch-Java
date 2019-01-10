/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utils;

/**
 *
 * @author filip
 */
public class ThreadUtils {
    public static void holdOn(double nanos) {
        long ti = System.nanoTime();
        long tf;
        do {
            tf = System.nanoTime();
            System.out.print("");
        } while (tf - ti < nanos);
    }
}
