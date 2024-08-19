const net = require('net');

// Store connected clients
const clients = {};

const server = net.createServer((socket) => {
  let deviceId;

  socket.on('data', (data) => {
    console.log(data);
    if (!data) return;
    const message = data.toString();
    console.log('message', message);
    const parsedMessage = JSON.parse(message);

    if (parsedMessage.action === 'register') {
      deviceId = parsedMessage.deviceId;
      let soc = socket;
      soc.on('data', (data) => {
        console.log(deviceId, data.toString());
      });
      clients[deviceId] = soc;
      console.log(`Device ${deviceId} registered`);
    } else if (parsedMessage.action === 'sendNotification') {
      const targetDeviceId = parsedMessage.targetDeviceId;
      const notification = parsedMessage.notification;

      if (clients[targetDeviceId]) {
        console.log(clients[targetDeviceId]);
        clients[targetDeviceId].write(
          JSON.stringify({ action: 'com.example.CUSTOM_ACTION', notification }),
        );

        clients[targetDeviceId].emit(
          'aaa',
          JSON.stringify({ action: 'com.example.CUSTOM_ACTION', notification }),
        );

        console.log(`Notification sent to ${targetDeviceId}`);
      } else {
        console.log(`Device ${targetDeviceId} not connected`);
      }
    }
  });

  socket.on('end', () => {
    if (deviceId) {
      delete clients[deviceId];
      console.log(`Device ${deviceId} disconnected`);
    }
  });
});

server.listen(12345, () => {
  console.log('Server running on port 12345');
});
