package com.demkom58.ids_lab_2.compute;

import com.demkom58.ids_lab_2.compute.task.Task;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Compute extends Remote {
    Object executeTask(Task task) throws RemoteException;
}
