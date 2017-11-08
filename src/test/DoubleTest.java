/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author kaeru
 */
public class DoubleTest {
	public static void main(String[] args) {
		for(int i=0; i < 1000; i++){
			Double p = (double)(i*100)/(1000-1);
			System.out.println(p);
		}
	}
}
