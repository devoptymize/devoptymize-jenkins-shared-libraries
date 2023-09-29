def call() {
  def script_render_template = libraryResource 'render-templates.py'
  writeFile file: "${env.WORKSPACE}/render-templates.py", text: script_render_template
  
  script {
    sh """
    /usr/bin/python3 ${env.WORKSPACE}/render-templates.py ${jinja2File} variable.yaml "${env.ENVIRONMENT}/${env.resource_name}-${env.aws_region}.yaml"
    """
  }
} 
