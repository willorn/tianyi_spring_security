package com.spring.statemachine.service;

// @Configuration
// @Slf4j
// public class Persist<E, S> {
//     @Resource
//     private RedisConnectionFactory redisConnectionFactory;
//
//     /**
//      * 持久化到内存map中
//      *
//      * @return
//      */
//     @Bean(name = "stateMachineMemPersister")
//     public static StateMachinePersister getPersister() {
//         return new DefaultStateMachinePersister(new StateMachinePersist() {
//             private Map map = new HashMap();
//
//             @Override
//             public void write(StateMachineContext context, Object contextObj) throws Exception {
//                 log.info("持久化状态机,context:{},contextObj:{}", JSON.toJSONString(context), JSON.toJSONString(contextObj));
//                 map.put(contextObj, context);
//             }
//
//             @Override
//             public StateMachineContext read(Object contextObj) throws Exception {
//                 log.info("获取状态机,contextObj:{}", JSON.toJSONString(contextObj));
//                 StateMachineContext stateMachineContext = (StateMachineContext) map.get(contextObj);
//                 log.info("获取状态机结果,stateMachineContext:{}", JSON.toJSONString(stateMachineContext));
//                 return stateMachineContext;
//             }
//         });
//     }
//
//     /**
//      * 持久化到redis中，在分布式系统中使用
//      *
//      * @return
//      */
//     @Bean(name = "stateMachineRedisPersister")
//     public RedisStateMachinePersister<E, S> getRedisPersister() {
//         RedisStateMachineContextRepository<E, S> repository = new RedisStateMachineContextRepository<>(redisConnectionFactory);
//         RepositoryStateMachinePersist p = new RepositoryStateMachinePersist<>(repository);
//         return new RedisStateMachinePersister<>(p);
//     }
// }  