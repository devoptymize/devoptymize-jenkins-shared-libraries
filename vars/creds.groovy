import Jenkinscred
def call(){
Jenkinscred cred = new Jenkinscred()
def String aws_creds = cred.creds()
return aws_creds
// println(aws_creds)
}
