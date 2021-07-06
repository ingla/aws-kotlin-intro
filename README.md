This project consists of two modules
- `hello-api` implements an AWS lambda function
- `infrastructure` defines the AWS resources to be deployed in AWS

Before reading on, please go to file `Assembly.kt` in infrastructure/src/main/...
and change the `label` to [your initials][AG start year], e.g. `IL18

### Build ###
The project uses `Gradle` to compile and package the code.
No installment of Gradle is necessary.

- Build and package module `hello-api` separately (will produce a 
  'production-aws.jar' file in hello-api/build/libs)
>  ./gradlew clean hello-api:shadowJar

- Build and package module `infrastructure`. This command will package
`hello-api` as well, because we have defined the task above as a 
  dependency to the following operation (see 
  infrastructure/build.gradle.kts).
> ./gradlew clean infrastructure:jar

### Deploy resources to AWS using CDK ###
#### Prerequisites ####

- AWS CDK (Cloud development kit) installed
  - For instance by following the instructions here https://docs.aws.amazon.com/cdk/latest/guide/cli.html
- Access to an AWS account
- AWS CLI (Command line tool) version 2 installed
  - For instance by following the instructions here https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2-mac.html#cliv2-mac-install-cmd

#### Activate access and log in to AWS ####
- Every ten hours --> Activate your access at https://portal.azure.com/#blade/Microsoft_Azure_PIMCommon/ActivationMenuBlade/aadgroup/provider/aadgroup
- Log in to AWS console
  - Log on to https://myapplications.microsoft.com/ with DNB credentials
  - Select AWS SSO --> your account --> Management console
  
- Get access to AWS from terminal (first time)
  - Run 
    > aws configure sso
  - Set `SSO start url` to https://dnbasa.awsapps.com/start
  - Set `SSO region` to eu-west-1
  - The rest of the fields can be set to default, but I recommend to set
  `CLI profile name` to a shorter name, e.g. `sand`.
  - The result of the configuration can be seen in file `~/.aws/config`
    
- Reactivate access from terminal (after activating PIM access)
  > aws sso login --profile [profilename]

#### Deploy to AWS using CDK ####
- CDK needs AWS credentials to be set before you can deploy from the terminal
  - Get credentials by logging in to https://myapplications.microsoft.com/
  - Select AWS SSO --> your account --> Command line or programmatic access
  - Copy the text from option 2) `Add a profile to your AWS credentials file`
  - Store the text in your `~/.aws/credentials` file. Remember to use the 
    same profile name for the account as you used in the config file.
    
- Check that everything works as expected by running
  > cdk list

This shoud give `ApiStack` as output
  
- Deploy to the ApiStack to AWS with
> cdk deploy --profile [profile name]

- Delete stack by
> cdk destroy --profile [profile name]
