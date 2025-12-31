from py_eureka_client import eureka_client as py_eureka_client

class EurekaClient:
    def __init__(self, eureka_server, app_name, instance_port):
        self.eureka_server = eureka_server
        self.app_name = app_name
        self.instance_port = instance_port

    async def start(self):
        await py_eureka_client.init_async(
            eureka_server=self.eureka_server,
            app_name=self.app_name,
            instance_port=self.instance_port
        )

    async def stop(self):
        await py_eureka_client.stop_async()
