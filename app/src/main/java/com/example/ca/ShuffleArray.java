package com.example.ca;

import java.util.Random;

public class ShuffleArray {

    static void randomize( int arr[], int n)
    {
        Random r = new Random();

        for (int i = n-1; i > 0; i--) {

            int j = r.nextInt(i);
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
}