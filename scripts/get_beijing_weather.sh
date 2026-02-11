#!/bin/bash
# 简单的北京天气获取脚本
# 使用wttr.in服务

echo "🌤️ **北京今日天气**"
echo ""
echo "正在尝试获取天气信息..."
echo ""

# 尝试获取简洁格式的天气信息
WEATHER=$(timeout 10 curl -s "wttr.in/Beijing?format=3" 2>/dev/null)

if [ -n "$WEATHER" ] && [ "$WEATHER" != "Weather data is not available for this location." ]; then
    echo "$WEATHER"
else
    echo "📍 北京"
    echo "❓ 天气信息暂时无法获取"
    echo ""
    echo "您可以稍后重试，或者访问 weather.com 获取最新天气。"
fi

echo ""
echo "这是您的Moltbot天气服务 🤖"