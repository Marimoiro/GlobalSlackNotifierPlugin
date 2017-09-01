
package org.jenkinsci.plugins.globalslack;

import hudson.model.Result;
import hudson.model.Descriptor.FormException;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import net.sf.json.JSONObject;

/*
input json

{
    "successRoom":"successRoom",
    "successMessage":"successMessage",
    "notifyOnSuccess":true,
    
    "failureRoom":"failureRoom",
    "failureMessage":"failureMessage",
    "notifyOnFail":true,

    "unstableRoom":"unstableRoom",
    "unstableMessage":"unstableMessage",
    "notifyOnUnstable":false,

    "notBuiltRoom":"notBuiltRoom",
    "notBuiltMessage":"notBuiltMessage",
    "notifyOnNotBuilt":true,

    "abortedRoom":"abortedRoom",
    "abortedMessage":"abortedMessage",
    "abortedOnSuccess":false
}
*/

public class GlobalSlackNotifierStub extends TestCase {

    
    public static GlobalSlackNotifier.DescriptorImpl getDescriptor(){

        String jsonString ="{\"successRoom\":\"successRoom\",\"successMessage\":\"successMessage\",\"notifyOnSuccess\":true,\"failureRoom\":\"failureRoom\",\"failureMessage\":\"failureMessage\",\"notifyOnFail\":true,\"unstableRoom\":\"unstableRoom\",\"unstableMessage\":\"unstableMessage\",\"notifyOnUnstable\":false,\"notBuiltRoom\":\"notBuiltRoom\",\"notBuiltMessage\":\"notBuiltMessage\",\"notifyOnNotBuilt\":true,\"abortedRoom\":\"abortedRoom\",\"abortedMessage\":\"abortedMessage\",\"notifyOnAborted\":false}";
        JSONObject jo = JSONObject.fromObject(jsonString);


        
        GlobalSlackNotifier.DescriptorImpl desc = new GlobalSlackNotifier.DescriptorImpl();

        desc.configure(jo);
        return desc;
        
    }
}