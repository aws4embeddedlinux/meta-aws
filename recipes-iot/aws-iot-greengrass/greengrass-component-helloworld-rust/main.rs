use std::{thread, time::Duration};
use gg_sdk::{Qos, Sdk};

fn main() {
    println!("[HelloWorldRust] COMPONENT STARTING");
    
    let sdk = Sdk::init();
    sdk.connect().expect("Failed to connect to GG nucleus");
    println!("[HelloWorldRust] Connected to GG nucleus");

    let _sub = sdk
        .subscribe_to_iot_core("hello", Qos::AtMostOnce, |topic, payload| {
            let payload_str = String::from_utf8_lossy(payload);
            println!("[HelloWorldRust] Received [{payload_str}] on [{topic}]");
        })
        .ok();

    let mut counter = 0;
    loop {
        counter += 1;
        let message = format!(r#"{{"message":"Hello from Rust!","counter":{}}}"#, counter);

        match sdk.publish_to_iot_core("hello", message.as_bytes(), Qos::AtMostOnce) {
            Ok(_) => println!("[HelloWorldRust] Published message #{}", counter),
            Err(e) => eprintln!("[HelloWorldRust] Publish failed: {:?}", e),
        }

        thread::sleep(Duration::from_secs(15));
    }
}
