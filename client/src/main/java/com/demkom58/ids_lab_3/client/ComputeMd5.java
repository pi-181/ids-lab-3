package com.demkom58.ids_lab_3.client;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ComputeMd5 {

    @SneakyThrows
    public static void main(String[] args) {
        final int totalShards = 4;

        final String start = "aaaaaa";
        final String end = "zzzzzz";
        final String target = "8d5f88b71d679934fdcdaf2ab4af0812"; // zzzzza

        final DataSlicer dataSlicer = new DataSlicer(totalShards, start, end);
        final String[] slices = dataSlicer.slice();
        System.out.println(Arrays.toString(slices));

        final long startTime = System.nanoTime();
        final Opt<String> result = distributedBruteforce(slices, new String[]{start, end}, target);
        final long endTime = System.nanoTime();

        System.out.println(
                result.map(r -> "Result found: " + r + " (" + target + ").").orElse("Result not found!")
                        + " For " + (endTime - startTime) + "ns"
        );
    }

    @SneakyThrows
    private static Opt<String> distributedBruteforce(String[] slices, String[] range, String target) {
        int threads = slices.length - 1;
        if (threads == 0) {
            System.out.println("To small thread count!");
            return Opt.empty();
        }

        final ExecutorService executorService = Executors.newFixedThreadPool(threads);
        List<Future<Opt<String>>> futures = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++) {
            final String[] slice = {slices[i], slices[i + 1]};
            final BruteforceMd5 task = new BruteforceMd5(slice, range, target);

            final int shard = i + 1;
            futures.add(executorService.submit(() -> {
                System.out.println("Compute " + shard + " started!");
                final Opt<String> stringOpt = task.execute();
                System.out.println("Compute " + shard + " done work!");
                return stringOpt;
            }));
        }

        while (true) {
            final Iterator<Future<Opt<String>>> iterator = futures.iterator();
            while (iterator.hasNext()) {
                final Future<Opt<String>> f = iterator.next();
                if (f.isDone()) {
                    final Opt<String> stringOpt = f.get();

                    if (stringOpt.isPresent()) {
                        futures.forEach(ft -> ft.cancel(true));
                        executorService.shutdownNow();
                        return stringOpt;
                    }

                    iterator.remove();
                }
            }

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
