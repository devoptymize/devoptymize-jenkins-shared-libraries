package Jenkinscred
import jenkins.*
import jenkins.model.* 
import hudson.*
import hudson.model.*
class Jenkinscred {
def jenkinsCredentials = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
        com.cloudbees.plugins.credentials.Credentials.class,
        Jenkins.instance,
        null,
        null
);
def creds(){
def String match = "_aws_"
def lst = []
for (creds in jenkinsCredentials) {
  if(creds.id.contains(match)){
    lst.add("'"+creds.id+"'")
    }
     }
  return ("return "+lst);
       }
  }
