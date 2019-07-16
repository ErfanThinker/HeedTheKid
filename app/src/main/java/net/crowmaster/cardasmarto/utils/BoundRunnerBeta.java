package net.crowmaster.cardasmarto.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import net.crowmaster.cardasmarto.nanohttpdServer.NanoHTTPD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by root on 8/16/16.
 */
public class BoundRunnerBeta implements NanoHTTPD.AsyncRunner {

    private final HandlerThread handlerThread;
    private final List<NanoHTTPD.ClientHandler> running =
            Collections.synchronizedList(new ArrayList<NanoHTTPD.ClientHandler>());
    private final Handler mHandler;

    public BoundRunnerBeta(HandlerThread handlerThread, Handler mHandler) {
        this.handlerThread = handlerThread;
        this.mHandler = mHandler;
    }

    @Override
    public void closeAll() {
        this.handlerThread.quit();
        // copy of the list for concurrency
        for (NanoHTTPD.ClientHandler clientHandler : new ArrayList<>(this.running)) {
            clientHandler.close();
        }
    }

    @Override
    public void closed(NanoHTTPD.ClientHandler clientHandler) {
        this.mHandler.removeMessages(this.running.indexOf(clientHandler) + 1);
        this.running.remove(clientHandler);
    }

    @Override
    public void exec(NanoHTTPD.ClientHandler clientHandler) {
        Message msg = new Message();
        msg.obj = clientHandler;
        msg.what = this.running.size() + 1;
        this.mHandler.sendMessage(msg);
        this.running.add(clientHandler);
    }
}
