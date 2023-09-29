#!/usr/bin/env groovy

def call(){
    sh '''
        wait_for_stack_creation() {
            status=$(aws cloudformation describe-stacks --stack-name $ENVIRONMENT-$resource_name-$resource --query "Stacks[0].StackStatus" --output text --region $aws_region)
            while [[ "$status" == "CREATE_IN_PROGRESS" ]]; do
                echo "Stack creation is still in progress. Waiting..."
                sleep 30  # Adjust the sleep interval (in seconds) as needed
                status=$(aws cloudformation describe-stacks --stack-name $ENVIRONMENT-$resource_name-$resource --query "Stacks[0].StackStatus" --output text --region $aws_region)
            done
            while [[ "$status" == "DELETE_IN_PROGRESS" ]]; do
                echo "Stack deletion is still in progress. Waiting..."
                sleep 30  # Adjust the sleep interval (in seconds) as needed
                status=$(aws cloudformation describe-stacks --stack-name $ENVIRONMENT-$resource_name-$resource --query "Stacks[0].StackStatus" --output text --region $aws_region) ||  true
            done
            status=$(aws cloudformation describe-stacks --stack-name $ENVIRONMENT-$resource_name-$resource --query "Stacks[0].StackStatus" --output text --region $aws_region) || true
        }
        status_check () {
            stack_info=$(aws cloudformation describe-stacks --stack-name $ENVIRONMENT-$resource_name-$resource --region $aws_region 2>&1) || true
            if [ "$status" = "CREATE_COMPLETE" ]; then
                echo "Stack creation completed. Current status: $status"
            elif [ "$stack_info" != *"Stack with id mystack does not exist"* ]; then
                echo "Stack deletion completed || stack doesn't exist. Current status: $status"
            else
                echo "Unknown status: $status"
                exit 1
            fi
        }
        echo "checking status of stack creation......."
        wait_for_stack_creation
        status_check
    '''
}
