LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := profiling
LOCAL_MODULE_TAGS := optional

LOCAL_FORCE_STATIC_EXECUTABLE := true
LOCAL_CPP_EXTENSION := .cc .cpp

LOCAL_PBC_FILES := \
                  src/google/protobuf/stubs/strutil.cc                		 	\
                  src/google/protobuf/stubs/substitute.cc             		 	\
                  src/google/protobuf/stubs/stringprintf.cc	          	    	\
                  src/google/protobuf/stubs/atomicops_internals_x86_gcc.cc		\
                  src/google/protobuf/stubs/structurally_valid.cc      			\
                  src/google/protobuf/descriptor.cc                    			\
                  src/google/protobuf/descriptor.pb.cc                 			\
                  src/google/protobuf/descriptor_database.cc           			\
                  src/google/protobuf/dynamic_message.cc               			\
                  src/google/protobuf/extension_set_heavy.cc           			\
                  src/google/protobuf/generated_message_reflection.cc  			\
                  src/google/protobuf/message.cc                       			\
                  src/google/protobuf/reflection_ops.cc                			\
                  src/google/protobuf/service.cc                       			\
                  src/google/protobuf/text_format.cc                   			\
                  src/google/protobuf/unknown_field_set.cc             			\
                  src/google/protobuf/wire_format.cc                   			\
                  src/google/protobuf/io/gzip_stream.cc                			\
                  src/google/protobuf/io/printer.cc                    			\
                  src/google/protobuf/io/tokenizer.cc                  			\
                  src/google/protobuf/io/zero_copy_stream_impl.cc      			\
                  src/google/protobuf/compiler/importer.cc             			\
                  src/google/protobuf/compiler/parser.cc               			\
                  src/google/protobuf/stubs/common.cc                  			\
                  src/google/protobuf/stubs/once.cc                    			\
                  src/google/protobuf/extension_set.cc                 			\
                  src/google/protobuf/generated_message_util.cc        			\
                  src/google/protobuf/message_lite.cc                  			\
                  src/google/protobuf/repeated_field.cc                			\
                  src/google/protobuf/wire_format_lite.cc              			\
                  src/google/protobuf/io/coded_stream.cc               			\
                  src/google/protobuf/io/zero_copy_stream.cc           			\
                  src/google/protobuf/io/zero_copy_stream_impl_lite.cc                  


LOCAL_SRC_FILES := 	\
					core.cc 													\
					src/ipc/ipcMessage.pb.cc									\
					src/ipc/ipcserver.cc										\
					src/profiling/cpuInfo.pb.cc								\
					src/profiling/networkInfo.pb.cc 						\
					src/profiling/osInfo.pb.cc 								\
					src/profiling/processInfo.pb.cc							\
					src/profiling/base.cc										\
					src/profiling/cpu.cc										\
					src/profiling/network.cc									\
					src/profiling/os.cc											\
					src/profiling/process.cc									\
					$(LOCAL_PBC_FILES) 
					 
LOCAL_C_INCLUDES := \
                   $(LOCAL_PATH)/include										\
                   $(LOCAL_PATH)/include/ipc									\
                   $(LOCAL_PATH)/include/profiling 								\
                   $(LOCAL_PATH)/include/profiling/gen							\
                   $(LOCAL_PATH)/src											\
                   $(JNI_H_INCLUDE)  
                   
LOCAL_LDLIBS := -lz -llog

# stlport conflicts with the host stl library
ifneq ($(TARGET_SIMULATOR),true)
LOCAL_C_INCLUDES += external/stlport/stlport
LOCAL_SHARED_LIBRARIES += libstlport
endif

LOCAL_CFLAGS := -DGOOGLE_PROTOBUF_NO_RTTI -D_GLIBCXX_PERMIT_BACKWARD_HASH 

include $(BUILD_EXECUTABLE)