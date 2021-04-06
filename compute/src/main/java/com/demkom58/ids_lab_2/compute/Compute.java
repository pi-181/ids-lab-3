package com.demkom58.ids_lab_2.compute;

import com.demkom58.ids_lab_2.compute.task.Task;
import com.demkom58.ids_lab_2.compute.util.Opt;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Compute extends Remote {
    Opt<String> executeTask(Task task) throws RemoteException;
}
