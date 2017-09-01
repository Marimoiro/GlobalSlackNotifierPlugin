
package org.jenkinsci.plugins.globalslack;

import hudson.model.Result;
import junit.framework.TestCase;
import net.sf.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


public class GlobalSlackNotifierTest extends TestCase {


    @Rule
    public final JenkinsRule rule = new JenkinsRule();
    
    public GlobalSlackNotifierTest(){

    }

    @Test
    public void testSuccessSlackMessage(){

        GlobalSlackNotifier.DescriptorImpl desc = GlobalSlackNotifierStub.getDescriptor();
        SlackMessage mes = desc.getSlackMessage(Result.SUCCESS);

        assertEquals(mes.getRoom(), "successRoom");
        assertEquals(mes.getMessage(), "successMessage");
        assertEquals(mes.getEnable(), true);
    }

    @Test
    public void testFailureSlackMessage(){
        SlackMessage mes = GlobalSlackNotifierStub.getDescriptor().getSlackMessage(Result.FAILURE);

        assertEquals(mes.getRoom(), "failureRoom");
        assertEquals(mes.getMessage(), "failureMessage");
        assertEquals(mes.getEnable(), true);
    }

    @Test
    public void testUnstableSlackMessage(){
        SlackMessage mes = GlobalSlackNotifierStub.getDescriptor().getSlackMessage(Result.UNSTABLE);

        assertEquals(mes.getRoom(), "unstableRoom");
        assertEquals(mes.getMessage(), "unstableMessage");
        assertEquals(mes.getEnable(), false);
    }


    @Test
    public void testNotBuiltSlackMessage(){
        SlackMessage mes = GlobalSlackNotifierStub.getDescriptor().getSlackMessage(Result.NOT_BUILT);

        assertEquals(mes.getRoom(), "notBuiltRoom");
        assertEquals(mes.getMessage(), "notBuiltMessage");
        assertEquals(mes.getEnable(), true);
    }

    @Test
    public void testAbortedSlackMessage(){
        SlackMessage mes = GlobalSlackNotifierStub.getDescriptor().getSlackMessage(Result.ABORTED);

        assertEquals(mes.getRoom(), "abortedRoom");
        assertEquals(mes.getMessage(), "abortedMessage");
        assertEquals(mes.getEnable(), false);
    }
}