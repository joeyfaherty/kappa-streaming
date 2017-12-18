#!/bin/bash

# tail a local log file 
# into netcat utility and expose it on PORT 9999

echo "script is tailing logs... available on port 9999"

tail -f ~/Library/Logs/IdeaIC2017.2/idea.log | nc -lk 9999


