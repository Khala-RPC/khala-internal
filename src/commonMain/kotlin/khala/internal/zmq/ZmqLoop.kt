package khala.internal.zmq

/*
ZmqLoop заморожен
При создании запускает поток, который будет мониторить сокеты
Поток создает свое состояние через loopStateProducer
Затем этот поток просто в бесконечном цикле делает poll по всем имеющимся сокетам

Хотим:
 - Добавлять сокеты из рандомного потока - addSafe
     -
 - Удалять сокеты из рандомного потока - removeSafe
 - Добавлять и удалять из коллбека внутри эвентлупа - addUnsafe, removeUnsafe
 - Посылать запрос, еще и с коллбеком при результате, из рандомного потока:
     - Изолейт стейт из inproc dealer сокета и arraydeque
     - в лупе забинженный inproc dealer сокет
     - при запросе он добавляется в arraydeque и законекченный сокет отправляет пустое сообщение на забинженный
     - луп затем получает это сообщение на забинженном и чекает arraydeque

 */
internal expect class ZmqLoop<S>(
    context: ZmqContext,
    userStateProducer: () -> S,
    forwardListener: LoopState<S>.(String, ZmqMsg) -> Unit,
    backwardListener: LoopState<S>.(ZmqMsg) -> Unit,
    backwardRouterBindAddress: String?
) {

    fun invokeSafe(
        block: (S) -> Unit
    )

    fun stopSafe()

}