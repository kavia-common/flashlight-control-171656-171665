#!/bin/bash
cd /home/kavia/workspace/code-generation/flashlight-control-171656-171665/flashlight_app_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

