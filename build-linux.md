
sudo apt install git build-essential gcc-multilib libc6-dev-i386 \
libncurses5 libczmq-dev libjson-c-dev libmsgpack-dev libnghttp2-dev \
libssl-dev

cd /usr/include/openssl
sudo ln -s /usr/include/gnutls/openssl.h .
sudo ln -s ../x86_64-linux-gnu/openssl/opensslconf.h .
