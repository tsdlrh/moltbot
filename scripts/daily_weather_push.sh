#!/bin/bash
# Daily weather push script for Beijing
# This script gets Beijing weather and sends it via Moltbot messaging

set -e

# Get Beijing weather using wttr.in (built-in weather skill)
WEATHER_INFO=$(curl -s "wttr.in/Beijing?format=3")

# Get more detailed weather if needed
DETAILED_WEATHER=$(curl -s "wttr.in/Beijing?format=%l:+%c+%t+%h+%w")

# Get today's date
DATE=$(date '+%Y-%m-%d %A')

# Create the weather message
MESSAGE="🌤️ **Daily Weather Report - Beijing** 🌤️

📅 $DATE

$WEATHER_INFO

📊 Details: $DETAILED_WEATHER

Have a great day! ☀️"

# Send the message using Moltbot's message tool
# Note: This will be executed by the cron system which has access to Moltbot's messaging
echo "$MESSAGE"