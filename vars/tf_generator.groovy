def generate_var(Map params, String path) {
  String match = "[";
  String match1 = "<";
  String match2 = '"';
  def filename = path+"/tfvars/${env.ENVIRONMENT}/${env.resource_name}_${env.ACCOUNT_ID}_${env.aws_region}.tfvars";
  File file = new File(filename)
  file.write "# This is dynamically generated tfvars file\n"
  for (entry in params){
    // The below if condition is to generate the list of the inputs which is used in ASG pipeline, if the input is starting with double quotes.
    if((entry.key).equals("LOAD_BALANCER") && (entry.value).contains(match2) ){
      println ((entry.key).toLowerCase()+"="+"[" + entry.value + "]")
      file.append(((entry.key).toLowerCase()+"="+ "[" + entry.value + "]")+"\n")
    }
    else if((entry.key).equals("LOAD_BALANCER") && (entry.value).equals("")){
      println ((entry.key).toLowerCase()+"= "+"[]")
      file.append(((entry.key).toLowerCase()+"= "+"[]")+"\n")
    }

    // The below if condition is to generate a empty list when the user doesnt select any security group or volume in launch template pipeline.
    else if((entry.key).equals("SECURITY_GROUP_ID") && (entry.value).equals("") || (entry.key).equals("VOLUME_SIZE") && (entry.value).equals("")|| (entry.key).equals("IP_ADDRESS") && (entry.value).equals("")){
      println ((entry.key).toLowerCase()+"="+"[]")
      file.append(((entry.key).toLowerCase()+"="+"[]")+"\n")
    }
    else if((entry.key).equals("DB_INSTANCE_ONE_CUSTOM") && (entry.value).contains(match2) || (entry.key).equals("DB_INSTANCE_TWO_CUSTOM") && (entry.value).contains(match2) || (entry.key).equals("DB_INSTANCE_THREE_CUSTOM") && (entry.value).contains(match2)){
      println ((entry.key).toLowerCase()+"="+"{" + entry.value + "}")
      file.append(((entry.key).toLowerCase()+"="+ "{" + entry.value + "}")+"\n")
    }
    // The below if condition is to generate a empty object when the user doesnt enter any custom configuration for the db instances for RDS cluster pipeline.
    else if((entry.key).equals("DB_INSTANCE_ONE_CUSTOM") && (entry.value).equals("") || (entry.key).equals("DB_INSTANCE_TWO_CUSTOM") && (entry.value).equals("") || (entry.key).equals("DB_INSTANCE_THREE_CUSTOM") && (entry.value).equals("")){
      println ((entry.key).toLowerCase()+"="+"{}")
      file.append(((entry.key).toLowerCase()+"="+"{}")+"\n")
    }
    // The below if condition allowed_http_method to generate tfvars inorder to get list of string in cloudfront pipeline
    else if((entry.key).equals("READ_ACCESS_ARNS") || (entry.key).equals("READ_WRITE_ACCESS_ARNS") || (entry.key).equals("ALLOWED_HTTP_METHODS") || (entry.key).equals("VALUE")){
      def inputString = entry.value
      def list = inputString.split(',')
      def formattedList = list.collect { "\"$it\"" }
      def result = "[" + formattedList.join(", ") + "]"
      println ((entry.key).toLowerCase()+"="+ result)
      file.append(((entry.key).toLowerCase()+"="+ result)+"\n")
    }
    // The below if condition is to generate tfvars when the input is starting with square brackets not to add double quotes.
    else if ((entry.value).contains(match) || (entry.value).contains(match1)){
      println ((entry.key).toLowerCase()+"="+entry.value)
      file.append(((entry.key).toLowerCase()+"="+entry.value)+"\n")
    }
    else{
      println ((entry.key).toLowerCase()+"="+'"'+entry.value+'"')   
      file.append(((entry.key).toLowerCase()+"="+'"'+entry.value+'"')+"\n")
    }
  }
  // The below shell command is remove the backslash from the tfvars file which will be added by the jenkins for the html formatted parameters.
  sh """
    sed -i -e  's/[\\]//g' ${path}//tfvars/${env.ENVIRONMENT}/${env.resource_name}_${env.ACCOUNT_ID}_${env.aws_region}.tfvars 
    #sed -i -e 's/\\(.*\\),/\\1/' ${path}//tfvars/${env.ENVIRONMENT}/${env.resource_name}_${env.ACCOUNT_ID}_${env.aws_region}.tfvars
  """
  sh """cd tfvars/${env.ENVIRONMENT} && terraform fmt"""
}

