def call(parameterMap) {
    node('pod') {
        container('pod') {
            stage('Checkout code') {
                sh('mkdir infra')
                dir('infra') {
                    sh("echo 'testContent' >> file.txt")
                    echo('Validating infra.yml')
                }
                stash name: 'infra-files', includes: 'infra/**', allowEmpty: true, useDefaultExcludes: false
            }
        }
    }
    function2()
    function1()
}

def addInfraDryRunStages(){
    Map<String, Closure> dryRunStages = [:]
    Map tempParameterMap = prepareInfraDryRunStages()
    dryRunStages.putAll(tempParameterMap)
    return dryRunStages
}


def prepareInfraDryRunStages(){
    parallelStage =  ["Dry run for": {
        stage("Executing infra dry run for "){
            dir("infra"){
                sh(label: 'Creating aws directory', script: "mkdir -p test", returnStatus:false)
                echo("Deleting aws directory")
                echo("Preparing aws credentials")
            }
        }
    }]
    return parallelStage
}

def function2() {
   Map<String, Closure> dryRunStages = addInfraDryRunStages()
   parallel dryRunStages   
}

def function1() {
    checkpoint('Start prod env deployment')

    stage('Milestone for prod deployment') {
        milestone 1
    }

    node('pod') {
        container('pod') {
            unstash 'infra-files'
            stage('Manual step for deploying changes to staging') {
                dir('infra') {
                    sh('cat file.txt')
                }
            }
        }
    }
}
