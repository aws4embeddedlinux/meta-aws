#include <gg/buffer.hpp>
#include <gg/ipc/client.hpp>
#include <gg/object.hpp>
#include <gg/types.hpp>
#include <chrono>
#include <iomanip>
#include <iostream>
#include <sstream>
#include <string>
#include <system_error>
#include <thread>
#include <sys/utsname.h>
#include <unistd.h>

class IoTCoreHandler : public gg::ipc::IotTopicCallback {
  public:
    void operator()(
        std::string_view topic,
        gg::Buffer payload,
        gg::ipc::Subscription &handle
    ) override {
        (void) handle;
        std::cout << "[HelloWorldCpp] RECEIVED on " << topic << ": ";
        std::cout.write(
            reinterpret_cast<const char *>(payload.data()),
            static_cast<std::streamsize>(payload.size())
        );
        std::cout << '\n';
    }
};

std::string get_platform() {
    struct utsname buf;
    if (uname(&buf) == 0) {
        return std::string(buf.sysname) + "-" + buf.release + "-" + buf.machine;
    }
    return "Unknown";
}

std::string get_timestamp() {
    auto now = std::chrono::system_clock::now();
    auto time = std::chrono::system_clock::to_time_t(now);
    auto ms = std::chrono::duration_cast<std::chrono::microseconds>(
        now.time_since_epoch()) % 1000000;
    
    std::ostringstream oss;
    oss << std::put_time(std::gmtime(&time), "%Y-%m-%dT%H:%M:%S")
        << '.' << std::setfill('0') << std::setw(6) << ms.count();
    return oss.str();
}

std::string get_thing_name(gg::ipc::Client &client) {
    const char* thing_name = std::getenv("AWS_IOT_THING_NAME");
    if (thing_name) {
        return thing_name;
    }
    return "unknown";
}

int main() {
    auto &client = gg::ipc::Client::get();
    
    auto error = client.connect();
    if (error) {
        std::cerr << "Failed to connect: " << error << '\n';
        return 1;
    }
    
    std::string platform = get_platform();
    std::string thing_name = get_thing_name(client);
    
    static IoTCoreHandler handler;
    error = client.subscribe_to_iot_core("hello", 0, handler);
    if (error) {
        std::cerr << "Failed to subscribe: " << error << '\n';
    }
    
    int counter = 0;
    while (true) {
        std::ostringstream json;
        json << "{\n"
             << "  \"message\": \"Hello world! Sent from Greengrass Core.\",\n"
             << "  \"platform\": \"" << platform << "\",\n"
             << "  \"timestamp\": \"" << get_timestamp() << "\",\n"
             << "  \"counter\": " << ++counter << ",\n"
             << "  \"component\": \"com.example.HelloWorldCpp\",\n"
             << "  \"version\": \"1.0.0\",\n"
             << "  \"device_id\": \"" << thing_name << "\",\n"
             << "  \"thing_name\": \"" << thing_name << "\",\n"
             << "  \"ipc_available\": true\n"
             << "}";
        
        std::string message = json.str();
        gg::Buffer payload(message);
        
        error = client.publish_to_iot_core("hello", payload, 0);
        if (error) {
            std::cerr << "[HelloWorldCpp] Publish failed: " << error << '\n';
        } else {
            std::cout << message << '\n';
        }
        
        using namespace std::chrono_literals;
        std::this_thread::sleep_for(5s);
    }
    
    return 0;
}
