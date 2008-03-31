/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright notice.  All rights reserved.
 */
package com.tc.statistics.retrieval.actions;

import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import com.tc.exception.TCRuntimeException;
import com.tc.statistics.StatisticData;
import com.tc.statistics.StatisticRetrievalAction;
import com.tc.statistics.StatisticType;

import java.util.ArrayList;
import java.util.List;

/**
 * This statistic gives the network activity going on in the system
 *
 * It contains {@link StatisticData} with the following elements:
 * <ul>
 * <li>bytes read</li>
 * <li>bytes written</li>
 */
public class SRANetworkActivity implements StatisticRetrievalAction {


  public final static String ACTION_NAME = "network activity";
  public static final String ELEMENT_BYTES_READ = "bytes read";
  public static final String ELEMENT_BYTES_WRITTEN = "bytes written";

  private final Sigar sigar;

  public SRANetworkActivity() {
    sigar = new Sigar();
  }

  public StatisticData[] retrieveStatisticData() {
    try {
      String[] iFaceList = sigar.getNetInterfaceList();
      long in = 0L;
      long out = 0L;
      for (int i = 0; i < iFaceList.length; i++) {
        String iFace = iFaceList[i];
        NetInterfaceStat stat = sigar.getNetInterfaceStat(iFace);
        in += stat.getRxBytes();
        out += stat.getTxBytes();
      }
      List data = new ArrayList();
      data.add(new StatisticData(ACTION_NAME, ELEMENT_BYTES_READ, new Long(in)));
      data.add(new StatisticData(ACTION_NAME, ELEMENT_BYTES_WRITTEN, new Long(out)));

      return (StatisticData[])data.toArray(new StatisticData[data.size()]);

    } catch (SigarException e) {
      throw new TCRuntimeException(e);
    }
  }

  public String getName() {
    return ACTION_NAME;
  }

  public StatisticType getType() {
    return StatisticType.SNAPSHOT;
  }
}