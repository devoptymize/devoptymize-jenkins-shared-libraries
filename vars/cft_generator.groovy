def generate_var(Map params, String path) {
  echo "VPCID"
  String match = "[";
  String match1 = "<";
  String match2 = '"';
  def filename = path+"/variable.yaml";
  File file = new File(filename)
  file.write "# This is dynamically generated variable file\n"
  for (entry in params){
    println ((entry.key).toLowerCase()+": "+ entry.value)
    // The below if condition is to generate the list of the inputs which is used in ASG pipeline, if the input is starting with double quotes.
    if((entry.key).equals("LOAD_BALANCER") && (entry.value).equals("")){
      println ((entry.key).toLowerCase()+": "+ entry.value)
      file.append(((entry.key).toLowerCase()+": "+ entry.value)+"\n")
    }
    // The below if condition is used in Route53 pipeline if the Route53_vpcid is empty list
    else if((entry.key).equals("ROUTE53_VPCID") && (ROUTE53_VPCID == [])){
      println ((entry.key).toLowerCase()+": ")
      file.append(((entry.key).toLowerCase()+": ")+"\n")
    }
    else if((entry.key).equals("VPCID") && (VPCID == "")){
     println ((entry.key).toLowerCase()+": ")
     file.append(((entry.key).toLowerCase()+": ")+"\n")
    }
    else if((entry.key).equals("LOAD_BALANCER") && (entry.value).contains(match2)){
      println ((entry.key).toLowerCase()+": "+ entry.value)
      file.append(((entry.key).toLowerCase()+": "+ entry.value)+"\n")
    }
// The below if condition is to generate a string from list of instance in case of Target group
    else if((entry.key).equals("EC2_ID") && (entry.value).contains(match)){
      def inputString = entry.value
      def cleanedString = inputString.replaceAll("[\\[\\]\"\\*\\.]", "")
      def subnetList = cleanedString.split(",").collect { it.trim() }
      def outputString = subnetList.join(", ")
      println outputString
      println ((entry.key).toLowerCase()+": "+'"'+outputString+'"')   
      file.append(((entry.key).toLowerCase()+": "+'"'+outputString+'"')+"\n")
    }
// The below if condition is to generate a string from list of string in case of Instance-schedular
    else if((entry.key).equals("INSTANCE_IDS") && (entry.value).contains(match)){
      def inputString = entry.value
      def cleanedString = inputString.replaceAll("[\\[\\]\"\\*\\.]", "")
      def subnetList = cleanedString.split(",").collect { it.trim() }
      def outputString = subnetList.join(", ")
      println outputString
      println ((entry.key).toLowerCase()+": "+'"'+outputString+'"')   
      file.append(((entry.key).toLowerCase()+": "+'"'+outputString+'"')+"\n")
    }
// The below if condition is to enclose a cron-job within '' for Instance-schedular
    else if((entry.key).equals("START_JOB") || (entry.key).equals("STOP_JOB")){
      def inputString = entry.value
      def cleanedString = inputString.replaceAll("[\\[\\]\"\\.]", "")
      def subnetList = cleanedString.split(",").collect { it.trim() }
      def outputString = subnetList.join(", ")
      println outputString
      println ((entry.key).toLowerCase()+": "+"'"+outputString+"'")   
      file.append(((entry.key).toLowerCase()+": "+"'"+outputString+"'")+"\n")
    }
    // The below if condition is to generate a empty list when the user doesnt select any security group or volume in launch template pipeline.
    else if((entry.key).equals("SECURITY_GROUP_ID") || (entry.key).equals("VOLUME_SIZE") || (entry.key).equals("SUBNET_ID") || (entry.key).equals("CERTIFICATE_NAME") ){
        if((entry.key).equals("SECURITY_GROUP_ID") && (entry.value).equals("") || (entry.key).equals("VOLUME_SIZE") && (entry.value).equals("") || (entry.key).equals("SUBNET_ID") && (entry.value).equals("") || (entry.key).equals("CERTIFICATE_NAME") && (entry.value).equals("") ){
            println ((entry.key).toLowerCase()+": "+entry.value)
            file.append(((entry.key).toLowerCase()+": "+entry.value)+"\n")
        }
        else {
            def inputString = entry.value
        //    def cleanedString = inputString.replaceAll("\\[|\\]|\"","")
            def cleanedString = inputString.replaceAll("[\\[\\]\"\\*\\.]", "")
            def subnetList = cleanedString.split(",").collect { it.trim() }
            def outputString = subnetList.join(", ")
            println outputString
            println ((entry.key).toLowerCase()+": "+'"'+outputString+'"')   
            file.append(((entry.key).toLowerCase()+": "+'"'+outputString+'"')+"\n")
        }
    }
    // The below if condition is to generate a empty object when the user doesnt enter any custom configuration for the db instances for RDS cluster pipeline.
    else if((entry.key).equals("DB_INSTANCE_ONE_CUSTOM") && (entry.value).equals("") || (entry.key).equals("DB_INSTANCE_TWO_CUSTOM") && (entry.value).equals("") || (entry.key).equals("DB_INSTANCE_THREE_CUSTOM") && (entry.value).equals("")){
      println ((entry.key).toLowerCase()+": "+"{}")
      file.append(((entry.key).toLowerCase()+": "+"{}")+"\n")
    }
    else if((entry.key).equals("SNS_TOPIC") && (entry.value).equals("null")){
      println ((entry.key).toLowerCase()+": "+"null")
      file.append(((entry.key).toLowerCase()+": "+"\"''\"")+"\n")
    }

    else if((entry.key).equals("ADDITIONAL_VOLUME_SIZE") && (entry.value).equals("")){
      println ((entry.key).toLowerCase()+": "+"\"''\"")
      file.append(((entry.key).toLowerCase()+": "+"\"''\"")+"\n")
    }
   
    // The below if condition is to generate variable when the input is starting with square brackets not to add double quotes.
    else if ((entry.value).contains(match) || (entry.value).contains(match1) || (entry.value).contains(match2)){
      println ((entry.key).toLowerCase()+": "+entry.value)
      file.append(((entry.key).toLowerCase()+": "+entry.value)+"\n")
    }
    else{
      println ((entry.key).toLowerCase()+": "+'"'+entry.value+'"')   
      file.append(((entry.key).toLowerCase()+": "+'"'+entry.value+'"')+"\n")
    }
  }
  // The below shell command is remove the backslash from the variable file which will be added by the jenkins for the html formatted parameters.
  sh """
    sed -i -e  's/[\\]//g' ${path}/variable.yaml
    #sed -i -e 's/\\(.*\\),/\\1/' ${path}/variable.yaml
  """
}

