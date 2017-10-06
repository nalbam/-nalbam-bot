#!groovy

//echo "JOB_NAME    ${env.JOB_NAME}"
//echo "BRANCH_NAME ${env.BRANCH_NAME}"

properties([
        buildDiscarder(logRotator(daysToKeepStr: '60', numToKeepStr: '10')),
        pipelineTriggers([[$class: "SCMTrigger", scmpoll_spec: "H/5 * * * *"]])
])

node {
    stage('Checkout') {
        checkout scm
    }

    stage('Build') {
        sh "~/toaster/toast.sh build version ${env.BRANCH_NAME}"
        try {
            mvn 'clean package -B -e'
            notify('Build Passed', 'good')
        } catch (e) {
            notify('Build Failed', 'danger')
            throw e
        }
    }

//    stage('Code Analysis') {
//        mvn 'checkstyle:checkstyle pmd:pmd pmd:cpd findbugs:findbugs -B -e'
//        step([$class: 'JUnitResultArchiver', testResults: 'target/surefire-reports/*.xml'])
//        step([$class: 'CheckStylePublisher', pattern: 'target/checkstyle-result.xml'])
//        step([$class: 'FindBugsPublisher', pattern: 'target/findbugsXml.xml'])
//        step([$class: 'PmdPublisher', pattern: 'target/pmd.xml'])
//        step([$class: 'DryPublisher', pattern: 'target/cpd.xml'])
//        step([$class: 'TasksPublisher', high: 'FIXME', low: '', normal: 'TODO', pattern: 'src/**/*.java, src/**/*.php'])
//    }

    stage('Publish') {
        sh "~/toaster/toast.sh build eb"
        archive 'target/*.jar, target/*.war, target/*.zip'
    }
}

// Run Maven from tool "mvn"
void mvn(args) {
    // Get the maven tool.
    // ** NOTE: This 'M3' maven tool must be configured
    // **       in the global configuration.
    def mvnHome = tool 'M3'

    sh "${mvnHome}/bin/mvn ${args}"
}

def notify(status, color) {
    if (color == 'danger' || env.BRANCH_NAME == 'master') {
        slackSend(color: color, message: "${status}: ${env.JOB_NAME} <${env.BUILD_URL}|#${env.BUILD_NUMBER}>")
    }
}
