package com.demkom58.ids_lab_2.client;

import com.demkom58.ids_lab_2.compute.Compute;
import com.demkom58.ids_lab_2.compute.util.Opt;
import lombok.SneakyThrows;

import java.nio.file.Paths;
import java.rmi.Naming;
import java.util.Arrays;

public class ComputeMd5 {
    private static final String SERVICE_NAME = "rmi://localhost/Compute";

    @SneakyThrows
    public static void main(String[] args) {
        applySecurityManager();

        final String start = "aaaaa";
        final String end = "zzzzz";
        final String target = "BF8133A199729DC9CC4DE62412DBBD2B";

        final DataSlicer dataSlicer = new DataSlicer(5, start, end);

        final String[] slices = dataSlicer.slice();

        final String[] slice = {slices[1], slices[2]};
        final String[] range = {start, end};

        BruteforceMd5 task = new BruteforceMd5(slice, range, target);
        Opt<String> result = task.execute();

        System.out.println("Slice: " + Arrays.toString(slice));
        System.out.println("Range: " + Arrays.toString(range));
        System.out.println(result.map(r -> "Result found: " + r).orElse("Result not found!"));


//        try {
//            Compute comp = (Compute) Naming.lookup(SERVICE_NAME);
//            BruteforceMd5 task = new BruteforceMd5(start, end, target);
//            Opt<String> result = comp.executeTask(task);
//
//            System.out.println(result.map(r -> "Result found: " + r).orElse("Result not found..."));
//        } catch (Exception e) {
//            System.err.println("ComputeMd5 exception: " + e.getMessage());
//            e.printStackTrace();
//        }
    }

    public static void applySecurityManager() {
        System.setProperty("java.security.policy", Paths.get(".", "rmi.policy").toAbsolutePath().toString());
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
    }
}
