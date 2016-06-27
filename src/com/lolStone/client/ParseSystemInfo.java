package com.lolStone.client;

import model.ClientInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;
import org.hyperic.sigar.cmd.SigarCommandBase;

/**
 * Created by root on 10.02.16.
 */
public class ParseSystemInfo  extends SigarCommandBase {

    public static void refresh(ClientInfo info)
    {
        try {
            new ParseSystemInfo(info);
        }catch (Exception e){}
    }

    public void output(String[] args){}

    public ParseSystemInfo(ClientInfo info) throws SigarException
    {
        Mem mem   = this.sigar.getMem();
        Swap swap = this.sigar.getSwap();
        info.setMem_free(mem.getFree());
        info.setMem_used(mem.getUsed());
        info.setSwap_free(swap.getFree());
        info.setSwap_used(swap.getUsed());
        CpuPerc cpu =this.sigar.getCpuPerc();

        info.setCPU_IdleTime(cpu.getIdle());
    }
}
