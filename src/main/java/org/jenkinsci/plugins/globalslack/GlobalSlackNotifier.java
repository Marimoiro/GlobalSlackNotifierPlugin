
package org.jenkinsci.plugins.globalslack;

import hudson.model.listeners.RunListener;
import hudson.model.*;
import hudson.EnvVars;
import hudson.Extension;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.AffectedFile;
import hudson.scm.ChangeLogSet.Entry;

import java.util.*;
import java.util.logging.Logger;

import jenkins.plugins.slack.*;



@Extension
public class GlobalSlackNotifier extends RunListener<Run<?, ?>> implements Describable<GlobalSlackNotifier> {

    private static final Logger logger = Logger.getLogger(GlobalSlackNotifier.class.getName());


    @Override
    public void onCompleted(Run<?, ?> run, TaskListener listener) {
        publish( run, listener);
    }


    public Descriptor<GlobalSlackNotifier> getDescriptor() {
        return getDescriptorImpl();
      }

      public DescriptorImpl getDescriptorImpl() {
        return (DescriptorImpl) Jenkins.getInstance().getDescriptorOrDie(GlobalSlackNotifier.class);
      }

      public SlackNotifier.DescriptorImpl getSlackDescriptor(){
        return (SlackNotifier.DescriptorImpl) Jenkins.getInstance().getDescriptor(SlackNotifier.class);
      }

      public SlackMessage getSlackMessage(Result result){
          return getDescriptorImpl().getSlackMessage(result);
      }

      public void publish(Run<?, ?> r, TaskListener listener)
      {
          Result result = r.getResult();
          SlackMessage message = getSlackMessage(result);
          SlackNotifier.DescriptorImpl slackDesc = getSlackDescriptor();

          if(!message.getEnable()){ return; }

          String teamDomain = slackDesc.getTeamDomain();

          String baseUrl = slackDesc.getBaseUrl();


          String authToken = slackDesc.getToken();
          boolean botUser = slackDesc.isBotUser();

          String authTokenCredentialId = slackDesc.getTokenCredentialId();
          String sendAs = slackDesc.getSendAs();


          String room = message.getRoom();
          if (StringUtils.isEmpty(room)) {
              room = slackDesc.getRoom();
          }
          if(StringUtils.isEmpty(room)){ return; }

          EnvVars env = null;
          try {
              env = r.getEnvironment(listener);
          } catch (Exception e) {
              listener.getLogger().println("Error retrieving environment vars: " + e.getMessage());
              env = new EnvVars();
          }

          baseUrl = env.expand(baseUrl);
          teamDomain = env.expand(teamDomain);
          authToken = env.expand(authToken);
          authTokenCredentialId = env.expand(authTokenCredentialId);
          room = env.expand(room);

          String postText = env.expand(message.getMessage());


          CommitInfoChoice choice = CommitInfoChoice.forDisplayName("nothing about commits"); //TODO :selectable
          // imcompletely
          SlackNotifier notifier = new SlackNotifier(baseUrl,teamDomain,authToken,botUser,room,authTokenCredentialId,
            sendAs,false,true,true,
            true,true,true,true,true,
            true,false,false,
            choice,!StringUtils.isEmpty(postText),postText, null, null, null, null, null);
          String messageText = getBuildStatusMessage(r,notifier,false,false,true);

          SlackService service = new StandardSlackService(baseUrl, teamDomain, authToken, authTokenCredentialId, botUser, room);
          boolean postResult = service.publish(messageText, message.getColor());
          if(!postResult){
              StringBuilder s = new StringBuilder("Global Slack Notifier try posting to slack. However some error occurred\n");
              s.append("TeamDomain :" + teamDomain + "\n");
              s.append("Channel :" + room + "\n");
              s.append("Message :" + postText + "\n");

              listener.getLogger().println(s.toString());
          }

        }

        /**
         * Copy from Slack Plugin's ActiveNotifier.getBuildStatusMessage & I changed AbstractBuild to Run
         * https://github.com/jenkinsci/slack-plugin/blob/master/src/main/java/jenkins/plugins/slack/ActiveNotifier.java#L256
         */
        String getBuildStatusMessage(Run<?,?> r,SlackNotifier notifier, boolean includeTestSummary, boolean includeFailedTests, boolean includeCustomMessage) {
            MessageBuilder message = new MessageBuilder(notifier, r);
            message.appendStatusMessage();
            message.appendDuration();
            message.appendOpenLink();
            if (includeTestSummary) {
                message.appendTestSummary();
            }
            if (includeFailedTests) {
                message.appendFailedTests();
            }
            if (includeCustomMessage) {
                message.appendCustomMessage();
            }
            return message.toString();
        }



      @Extension
      public static final class DescriptorImpl extends Descriptor<GlobalSlackNotifier> {

        private String successRoom;
        private String successMessage;
        private boolean notifyOnSuccess;

        private String failureRoom;
        private String failureMessage;
        private boolean notifyOnFail;

        private String unstableRoom;
        private String unstableMessage;
        private boolean notifyOnUnstable;

        private String notBuiltRoom;
        private String notBuiltMessage;
        private boolean notifyOnNotBuilt;

        private String abortedRoom;
        private String abortedMessage;
        private boolean notifyOnAborted;

        private SlackMessage successSlackMessage;
        private SlackMessage failureSlackMessage;
        private SlackMessage unstableSlackMessage;
        private SlackMessage notBuiltSlackMessage;
        private SlackMessage abortedSlackMessage;

        public DescriptorImpl() {
            try{
                load();
            }catch(NullPointerException e)
            {

            }
        }
        public String getDisplayName() {
            return "Global Slack Messages";
        }

        public SlackMessage getSlackMessage(Result result){

            if(result == Result.SUCCESS)
            {
                return successSlackMessage;
            }else if(result == Result.FAILURE){
                return failureSlackMessage;
            }else if(result == Result.UNSTABLE){
                return unstableSlackMessage;
            }else if(result == Result.NOT_BUILT){
                return notBuiltSlackMessage;
            }else if(result == Result.ABORTED){
                return abortedSlackMessage;
            }
            throw new IllegalArgumentException("result not match");
        }

        public void configure(JSONObject formData){
            successSlackMessage = new SlackMessage(successRoom = formData.getString("successRoom"), successMessage = formData.getString("successMessage"), notifyOnSuccess = formData.getBoolean("notifyOnSuccess"),"good");
            failureSlackMessage = new SlackMessage(failureRoom = formData.getString("failureRoom"), failureMessage = formData.getString("failureMessage"), notifyOnFail = formData.getBoolean("notifyOnFail"),"danger");
            unstableSlackMessage = new SlackMessage(unstableRoom = formData.getString("unstableRoom"), unstableMessage = formData.getString("unstableMessage"), notifyOnUnstable =  formData.getBoolean("notifyOnUnstable"),"warning");
            notBuiltSlackMessage = new SlackMessage(notBuiltRoom = formData.getString("notBuiltRoom"), notBuiltMessage = formData.getString("notBuiltMessage"), notifyOnNotBuilt = formData.getBoolean("notifyOnNotBuilt"),"gray");
            abortedSlackMessage = new SlackMessage(abortedRoom =formData.getString("abortedRoom"), abortedMessage = formData.getString("abortedMessage"), notifyOnAborted = formData.getBoolean("notifyOnAborted"),"warning");

        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws Descriptor.FormException {

            configure(formData);

            save();
            return super.configure(req, formData);
        }

		/**
		 * @return the successRoom
		 */
		public String getSuccessRoom() {
			return successRoom;
		}
		/**
		 * @return the successMessage
		 */
		public String getSuccessMessage() {
			return successMessage;
		}

		/**
		 * @return the notifyOnSuccess
		 */
		public boolean isNotifyOnSuccess() {
			return notifyOnSuccess;
		}

		/**
		 * @return the failureRoom
		 */
		public String getFailureRoom() {
			return failureRoom;
		}

		/**
		 * @return the failureMessage
		 */
		public String getFailureMessage() {
			return failureMessage;
		}

		/**
		 * @return the notifyOnFail
		 */
		public boolean isNotifyOnFail() {
			return notifyOnFail;
		}

		/**
		 * @return the unstableRoom
		 */
		public String getUnstableRoom() {
			return unstableRoom;
		}

		/**
		 * @return the unstableMessage
		 */
		public String getUnstableMessage() {
			return unstableMessage;
		}

		/**
		 * @return the notifyOnUnstable
		 */
		public boolean isNotifyOnUnstable() {
			return notifyOnUnstable;
		}

		/**
		 * @return the notBuiltRoom
		 */
		public String getNotBuiltRoom() {
			return notBuiltRoom;
		}

		/**
		 * @return the notBuiltMessage
		 */
		public String getNotBuiltMessage() {
			return notBuiltMessage;
		}

		/**
		 * @return the notifyOnNotBuilt
		 */
		public boolean isNotifyOnNotBuilt() {
			return notifyOnNotBuilt;
		}

		/**
		 * @return the abortedRoom
		 */
		public String getAbortedRoom() {
			return abortedRoom;
		}

		/**
		 * @return the abortedMessage
		 */
		public String getAbortedMessage() {
			return abortedMessage;
        }

		/**
		 * @return the notifyOnAborted
		 */
		public boolean isNotifyOnAborted() {
			return notifyOnAborted;
		}


      }
}
