var mqtt = require('mqtt');
require('dotenv').config()

// Create a client connection
var client = mqtt.connect('mqtt://localhost:1884', {
    username: process.env.MQTT_USER,
    password: process.env.MQTT_PASS
});

client.on('connect', function() { // When connected

  // subscribe to a topic
  client.subscribe('hello/world', function() {
    // when a message arrives, do something with it
    client.on('message', function(topic, message, packet) {
      console.log("Received '" + message + "' on '" + topic + "'");
    });
  });

  // publish a message to a topic
  client.publish('hello/world', 'my message', function() {
    console.log("Message is published");
    client.end(); // Close the connection when published
  });
});