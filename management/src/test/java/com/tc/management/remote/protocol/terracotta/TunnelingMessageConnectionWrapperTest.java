/* 
 * The contents of this file are subject to the Terracotta Public License Version
 * 2.0 (the "License"); You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at 
 *
 *      http://terracotta.org/legal/terracotta-public-license.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Covered Software is Terracotta Platform.
 *
 * The Initial Developer of the Covered Software is 
 *      Terracotta, Inc., a Software AG company
 */
package com.tc.management.remote.protocol.terracotta;

import org.junit.Assert;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.tc.io.TCByteBufferOutputStream;
import com.tc.net.protocol.tcm.MessageChannel;
import com.tc.net.protocol.tcm.MessageMonitor;
import com.tc.net.protocol.tcm.TCMessage;
import com.tc.net.protocol.tcm.TCMessageType;
import com.tc.object.session.SessionID;
import com.tc.test.TCTestCase;
import com.tc.util.concurrent.ThreadUtil;
import com.tc.util.runtime.ThreadDumpUtil;

import javax.management.remote.message.Message;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static com.tc.util.runtime.ThreadDumpUtil.getAllThreads;
import static java.util.Arrays.stream;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TunnelingMessageConnectionWrapperTest extends TCTestCase {
  private static Thread[] getAllNonSystemThreads() {
    List<Thread> allThreads = stream(getAllThreads()).filter(t -> !t.getThreadGroup().getName().equals("system"))
      .collect(Collectors.toList());
    return allThreads.toArray(new Thread[0]);
  }

  public void testClose() throws Exception {
    Thread[] initialThreads = getAllNonSystemThreads();
    MessageChannel mockChanel = mock(MessageChannel.class);
    final CountDownLatch messageSendLatch = new CountDownLatch(1);
    when(mockChanel.createMessage(any())).then(new Answer<TCMessage>() {

      @Override
      public TCMessage answer(InvocationOnMock invocation) throws Throwable {
        return new JmxRemoteTunnelMessage(new SessionID(0), mock(MessageMonitor.class),
            new TCByteBufferOutputStream(), null, TCMessageType.JMXREMOTE_MESSAGE_CONNECTION_MESSAGE) {
          @Override
          public void send() {
            messageSendLatch.countDown();
          }
        };
      }
    }
    );
    TunnelingMessageConnection connection = new TunnelingMessageConnectionWrapper(mockChanel,
                                                                                  true);
    connection.writeMessage(mock(Message.class));
    // wait until the message is send..
    messageSendLatch.await();
    connection.close();
    ThreadUtil.reallySleep(2000);
    Thread[] finalThreads = getAllNonSystemThreads();
    if (finalThreads.length > initialThreads.length) {
      System.out.println(ThreadDumpUtil.getThreadDump());
      Assert.fail();
    }
  }


}