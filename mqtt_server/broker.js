const aedes = require('aedes')()
const server = require('net').createServer(aedes.handle)
const httpServer = require('http').createServer()
const ws = require('websocket-stream')
require('dotenv').config()
const { MongoClient } = require("mongodb");

MQTT_Port = 1884
const wsPort = 8884

const mongo_uri = "mongodb+srv://" + process.env.MONGO_USER + ":" + process.env.MONGO_PASS + "@" + process.env.MONGO_CLUSTER + "/?retryWrites=true&w=majority";
const mongo_client = new MongoClient(mongo_uri);

server.listen(MQTT_Port, function() {
    console.log('Aedes MQTT server started and listening on port', MQTT_Port)
}), ws.createServer({
    server: httpServer
}, aedes.handle)
httpServer.listen(wsPort, function() {
    console.log('websocket server listening on port ', wsPort)
})

// authentication
aedes.authenticate = (client, username, password, callback) => {
    password = Buffer.from(password, 'base64').toString();
    if (username === process.env.MQTT_USER && password === process.env.MQTT_PASS) {
        return callback(null, true);
    }
    const error = new Error('Authentication Failed!! Please enter valid credentials.');
    console.log('Authentication failed.')
    return callback(error, false)
}
// authorising client topic to publish a message
aedes.authorizePublish = (client, packet, callback) => {
    if (packet.topic === 'abc') {
        return callback(new Error('wrong topic'));
    }
    if (packet.topic === 'charcha') {
        packet.payload = Buffer.from('overwrite packet payload')
    }
    callback(null)
}

// emitted when a client connects to the broker
aedes.on('client', function(client) {
    console.log(`CLIENT_CONNECTED : MQTT Client ${(client ? client.id : client)} connected to aedes broker ${aedes.id}`)
})
// emitted when a client disconnects from the broker
aedes.on('clientDisconnect', function(client) {
    console.log(`CLIENT_DISCONNECTED : MQTT Client ${(client ? client.id : client)} disconnected from the aedes broker ${aedes.id}`)
})
// emitted when a client subscribes to a message topic
aedes.on('subscribe', function(subscriptions, client) {
    console.log(`TOPIC_SUBSCRIBED : MQTT Client ${(client ? client.id : client)} subscribed to topic: ${subscriptions.map(s => s.topic).join(',')} on aedes broker ${aedes.id}`)
})
// emitted when a client unsubscribes from a message topic
aedes.on('unsubscribe', function(subscriptions, client) {
    console.log(`TOPIC_UNSUBSCRIBED : MQTT Client ${(client ? client.id : client)} unsubscribed to topic: ${subscriptions.join(',')} from aedes broker ${aedes.id}`)
})
// emitted when a client publishes a message packet on the topic
aedes.on('publish', async function(packet, client) {
    if (client) {
        try {
            if(packet.topic == 'temperature_update') {
                await mongo_client.connect();
                const parts = String(packet.payload).split("|");
                const temperature = parts[0];
                const time = parts[1];
                const location = parts[2];
                const doc = {
                    temperature: temperature,
                    time: time,
                    location: location,
                    client: (client ? client.id : 'AEDES BROKER_' + aedes.id),
                    broker: aedes.id
                }

                const insertResult = await mongo_client.db(process.env.MONGO_DATABASE).collection('temperature_updates').insertOne(doc);
                console.log(
                   `A document was inserted with the _id: ${insertResult.insertedId}`,
                );
            }
        } finally {
            await mongo_client.close();
        }
        console.log(`MESSAGE_PUBLISHED : MQTT Client ${(client ? client.id : 'AEDES BROKER_' + aedes.id)} has published message "${packet.payload}" on ${packet.topic} to aedes broker ${aedes.id}`)
    }
})