def call(parameterMap) {
  node("pod") {
    stage("Checkout code") {
      Map tempParameterMap = [: ]
      sh("echo 'testContent' >> file.txt")
      stash name: "infra-files", includes: "*.txt", allowEmpty: true, useDefaultExcludes: false
    }
    stage('Paralel inside node') {
      container('pod') {
      parallel([
        hello: {
          echo "hello"
        },
        world: {
          echo "world"
        }
      ])
    }
  }
}
  function1()
}

def function1() {
  checkpoint("Start prod env deployment")

  stage("Milestone for prod deployment") {
    milestone 1
  }

  node("pod") {
    unstash "infra-files"
    stage("Manual step for deploying changes to staging") {
      sh("cat file.txt")
    }
  }
}
