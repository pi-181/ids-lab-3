package com.demkom58.ids_lab_2.client;

import com.demkom58.ids_lab_2.compute.Compute;
import com.demkom58.ids_lab_2.compute.util.Opt;
import lombok.SneakyThrows;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ComputeMd5 {
    private static final String SERVICE_NAME = "rmi://localhost/Compute";

    @SneakyThrows
    public static void main(String[] args) {
        final int totalShards = 4;

        final String start = "aaaaaa";
        final String end = "zzzzzz";
        final String target = "03e76a317e61c421affc25aa92895980";

        final DataSlicer dataSlicer = new DataSlicer(totalShards, start, end);
        final String[] slices = dataSlicer.slice();
        System.out.println(Arrays.toString(slices));

        final List<Compute> shards = lookupShards(totalShards);

        final long startTime = System.nanoTime();
        final Opt<String> result = distributedBruteforce(shards, slices, new String[] {start, end}, target);
        final long endTime = System.nanoTime();

        System.out.println(
                result.map(r -> "Result found: " + r + ".").orElse("Result not found!")
                        + " For " + (endTime - startTime) + "ns"
        );
    }

    private static List<Compute> lookupShards(int totalShards) {
        final List<Compute> computes = new ArrayList<>();
        for (int i = 0; i < totalShards; i++) {
            try {
                final String name = SERVICE_NAME + (i + 1);
                computes.add((Compute) Naming.lookup(name));
                System.out.println("Found compute host: " + name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return computes;
    }

    @SneakyThrows
    private static Opt<String> distributedBruteforce(List<Compute> computes, String[] slices, String[] range, String target) {
        if (computes.isEmpty()) {
            System.out.println("Not found available compute hosts!");
            return Opt.empty();
        }
        final ExecutorService executorService = Executors.newFixedThreadPool(computes.size());
        List<Future<Opt<String>>> futures = new ArrayList<>(computes.size());
        for (int i = 0; i < computes.size(); i++) {
            final Compute compute = computes.get(i);
            final String[] slice = {slices[i], slices[i + 1]};
            final BruteforceMd5 task = new BruteforceMd5(slice, range, target);

            final int shard = i + 1;
            futures.add(executorService.submit(() -> {
                try {
                    System.out.println("Compute " + shard + " started!");
                    final Opt<String> stringOpt = compute.executeTask(task);
                    System.out.println("Compute " + shard + " done work!");
                    return stringOpt;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
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
