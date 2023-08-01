#!/usr/bin/env node
import * as cdk from 'aws-cdk-lib';
import { LambdaStack } from '../lib/java-lambda';
import { AdotCollectorStack } from '../lib/adot-collector';

const app = new cdk.App();
new AdotCollectorStack(app, 'AdotCollectorStack');
new LambdaStack(app, 'LambdaStack');
