#!/bin/bash
# Simple daily weather push script for Beijing

# Get weather using wttr.in (built into Moltbot)
WEATHER_OUTPUT=$(curl -s "wttr.in/Beijing?format=%l:+%c+%t+%h+%w" 2>/dev/null)

if [ -z "$WEATHER_OUTPUT" ]; then
    WEATHER_OUTPUT="北京: 🌤️ 获取天气信息失败，请稍后重试"
fi

# Send weather message
echo "🌤️ **北京今日天气**"
echo ""
echo "$WEATHER_OUTPUT"
echo ""
echo "这是您的每日天气推送服务 📅"