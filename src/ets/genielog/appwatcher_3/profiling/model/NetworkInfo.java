// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: networkInfo.proto

package ets.genielog.appwatcher_3.profiling.model;

public final class NetworkInfo {
	private NetworkInfo() {
	}

	public static void registerAllExtensions(
			com.google.protobuf.ExtensionRegistry registry) {
	}

	public interface networkInfoOrBuilder extends
			com.google.protobuf.MessageOrBuilder {

		// required string name = 1;
		/**
		 * <code>required string name = 1;</code>
		 * 
		 * <pre>
		 * * interface name
		 * </pre>
		 */
		boolean hasName();

		/**
		 * <code>required string name = 1;</code>
		 * 
		 * <pre>
		 * * interface name
		 * </pre>
		 */
		java.lang.String getName();

		/**
		 * <code>required string name = 1;</code>
		 * 
		 * <pre>
		 * * interface name
		 * </pre>
		 */
		com.google.protobuf.ByteString getNameBytes();

		// required uint32 flags = 2;
		/**
		 * <code>required uint32 flags = 2;</code>
		 * 
		 * <pre>
		 * * status flag
		 * </pre>
		 */
		boolean hasFlags();

		/**
		 * <code>required uint32 flags = 2;</code>
		 * 
		 * <pre>
		 * * status flag
		 * </pre>
		 */
		int getFlags();
	}

	/**
	 * Protobuf type {@code Profiling.core.networkInfo}
	 */
	public static final class networkInfo extends
			com.google.protobuf.GeneratedMessage implements
			networkInfoOrBuilder {
		// Use networkInfo.newBuilder() to construct.
		private networkInfo(
				com.google.protobuf.GeneratedMessage.Builder<?> builder) {
			super(builder);
			this.unknownFields = builder.getUnknownFields();
		}

		private networkInfo(boolean noInit) {
			this.unknownFields = com.google.protobuf.UnknownFieldSet
					.getDefaultInstance();
		}

		private static final networkInfo defaultInstance;

		public static networkInfo getDefaultInstance() {
			return defaultInstance;
		}

		public networkInfo getDefaultInstanceForType() {
			return defaultInstance;
		}

		private final com.google.protobuf.UnknownFieldSet unknownFields;

		@java.lang.Override
		public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
			return this.unknownFields;
		}

		private networkInfo(com.google.protobuf.CodedInputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			initFields();
			com.google.protobuf.UnknownFieldSet.Builder unknownFields = com.google.protobuf.UnknownFieldSet
					.newBuilder();
			try {
				boolean done = false;
				while (!done) {
					int tag = input.readTag();
					switch (tag) {
					case 0:
						done = true;
						break;
					default: {
						if (!parseUnknownField(input, unknownFields,
								extensionRegistry, tag)) {
							done = true;
						}
						break;
					}
					case 10: {
						bitField0_ |= 0x00000001;
						name_ = input.readBytes();
						break;
					}
					case 16: {
						bitField0_ |= 0x00000002;
						flags_ = input.readUInt32();
						break;
					}
					}
				}
			} catch (com.google.protobuf.InvalidProtocolBufferException e) {
				throw e.setUnfinishedMessage(this);
			} catch (java.io.IOException e) {
				throw new com.google.protobuf.InvalidProtocolBufferException(
						e.getMessage()).setUnfinishedMessage(this);
			} finally {
				this.unknownFields = unknownFields.build();
				makeExtensionsImmutable();
			}
		}

		public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
			return ets.genielog.appwatcher_3.profiling.model.NetworkInfo.internal_static_Profiling_core_networkInfo_descriptor;
		}

		protected com.google.protobuf.GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
			return ets.genielog.appwatcher_3.profiling.model.NetworkInfo.internal_static_Profiling_core_networkInfo_fieldAccessorTable
					.ensureFieldAccessorsInitialized(
							ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo.class,
							ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo.Builder.class);
		}

		public static com.google.protobuf.Parser<networkInfo> PARSER = new com.google.protobuf.AbstractParser<networkInfo>() {
			public networkInfo parsePartialFrom(
					com.google.protobuf.CodedInputStream input,
					com.google.protobuf.ExtensionRegistryLite extensionRegistry)
					throws com.google.protobuf.InvalidProtocolBufferException {
				return new networkInfo(input, extensionRegistry);
			}
		};

		@java.lang.Override
		public com.google.protobuf.Parser<networkInfo> getParserForType() {
			return PARSER;
		}

		private int bitField0_;
		// required string name = 1;
		public static final int NAME_FIELD_NUMBER = 1;
		private java.lang.Object name_;

		/**
		 * <code>required string name = 1;</code>
		 * 
		 * <pre>
		 * * interface name
		 * </pre>
		 */
		public boolean hasName() {
			return ((bitField0_ & 0x00000001) == 0x00000001);
		}

		/**
		 * <code>required string name = 1;</code>
		 * 
		 * <pre>
		 * * interface name
		 * </pre>
		 */
		public java.lang.String getName() {
			java.lang.Object ref = name_;
			if (ref instanceof java.lang.String) {
				return (java.lang.String) ref;
			} else {
				com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
				java.lang.String s = bs.toStringUtf8();
				if (bs.isValidUtf8()) {
					name_ = s;
				}
				return s;
			}
		}

		/**
		 * <code>required string name = 1;</code>
		 * 
		 * <pre>
		 * * interface name
		 * </pre>
		 */
		public com.google.protobuf.ByteString getNameBytes() {
			java.lang.Object ref = name_;
			if (ref instanceof java.lang.String) {
				com.google.protobuf.ByteString b = com.google.protobuf.ByteString
						.copyFromUtf8((java.lang.String) ref);
				name_ = b;
				return b;
			} else {
				return (com.google.protobuf.ByteString) ref;
			}
		}

		// required uint32 flags = 2;
		public static final int FLAGS_FIELD_NUMBER = 2;
		private int flags_;

		/**
		 * <code>required uint32 flags = 2;</code>
		 * 
		 * <pre>
		 * * status flag
		 * </pre>
		 */
		public boolean hasFlags() {
			return ((bitField0_ & 0x00000002) == 0x00000002);
		}

		/**
		 * <code>required uint32 flags = 2;</code>
		 * 
		 * <pre>
		 * * status flag
		 * </pre>
		 */
		public int getFlags() {
			return flags_;
		}

		private void initFields() {
			name_ = "";
			flags_ = 0;
		}

		private byte memoizedIsInitialized = -1;

		public final boolean isInitialized() {
			byte isInitialized = memoizedIsInitialized;
			if (isInitialized != -1)
				return isInitialized == 1;

			if (!hasName()) {
				memoizedIsInitialized = 0;
				return false;
			}
			if (!hasFlags()) {
				memoizedIsInitialized = 0;
				return false;
			}
			memoizedIsInitialized = 1;
			return true;
		}

		public void writeTo(com.google.protobuf.CodedOutputStream output)
				throws java.io.IOException {
			getSerializedSize();
			if (((bitField0_ & 0x00000001) == 0x00000001)) {
				output.writeBytes(1, getNameBytes());
			}
			if (((bitField0_ & 0x00000002) == 0x00000002)) {
				output.writeUInt32(2, flags_);
			}
			getUnknownFields().writeTo(output);
		}

		private int memoizedSerializedSize = -1;

		public int getSerializedSize() {
			int size = memoizedSerializedSize;
			if (size != -1)
				return size;

			size = 0;
			if (((bitField0_ & 0x00000001) == 0x00000001)) {
				size += com.google.protobuf.CodedOutputStream.computeBytesSize(
						1, getNameBytes());
			}
			if (((bitField0_ & 0x00000002) == 0x00000002)) {
				size += com.google.protobuf.CodedOutputStream
						.computeUInt32Size(2, flags_);
			}
			size += getUnknownFields().getSerializedSize();
			memoizedSerializedSize = size;
			return size;
		}

		private static final long serialVersionUID = 0L;

		@java.lang.Override
		protected java.lang.Object writeReplace()
				throws java.io.ObjectStreamException {
			return super.writeReplace();
		}

		public static ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo parseFrom(
				com.google.protobuf.ByteString data)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data);
		}

		public static ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo parseFrom(
				com.google.protobuf.ByteString data,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data, extensionRegistry);
		}

		public static ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo parseFrom(
				byte[] data)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data);
		}

		public static ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo parseFrom(
				byte[] data,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return PARSER.parseFrom(data, extensionRegistry);
		}

		public static ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo parseFrom(
				java.io.InputStream input) throws java.io.IOException {
			return PARSER.parseFrom(input);
		}

		public static ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo parseFrom(
				java.io.InputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws java.io.IOException {
			return PARSER.parseFrom(input, extensionRegistry);
		}

		public static ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo parseDelimitedFrom(
				java.io.InputStream input) throws java.io.IOException {
			return PARSER.parseDelimitedFrom(input);
		}

		public static ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo parseDelimitedFrom(
				java.io.InputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws java.io.IOException {
			return PARSER.parseDelimitedFrom(input, extensionRegistry);
		}

		public static ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo parseFrom(
				com.google.protobuf.CodedInputStream input)
				throws java.io.IOException {
			return PARSER.parseFrom(input);
		}

		public static ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo parseFrom(
				com.google.protobuf.CodedInputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws java.io.IOException {
			return PARSER.parseFrom(input, extensionRegistry);
		}

		public static Builder newBuilder() {
			return Builder.create();
		}

		public Builder newBuilderForType() {
			return newBuilder();
		}

		public static Builder newBuilder(
				ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo prototype) {
			return newBuilder().mergeFrom(prototype);
		}

		public Builder toBuilder() {
			return newBuilder(this);
		}

		@java.lang.Override
		protected Builder newBuilderForType(
				com.google.protobuf.GeneratedMessage.BuilderParent parent) {
			Builder builder = new Builder(parent);
			return builder;
		}

		/**
		 * Protobuf type {@code Profiling.core.networkInfo}
		 */
		public static final class Builder extends
				com.google.protobuf.GeneratedMessage.Builder<Builder> implements
				ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfoOrBuilder {
			public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
				return ets.genielog.appwatcher_3.profiling.model.NetworkInfo.internal_static_Profiling_core_networkInfo_descriptor;
			}

			protected com.google.protobuf.GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
				return ets.genielog.appwatcher_3.profiling.model.NetworkInfo.internal_static_Profiling_core_networkInfo_fieldAccessorTable
						.ensureFieldAccessorsInitialized(
								ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo.class,
								ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo.Builder.class);
			}

			// Construct using
			// Profiling.core.NetworkInfo.networkInfo.newBuilder()
			private Builder() {
				maybeForceBuilderInitialization();
			}

			private Builder(
					com.google.protobuf.GeneratedMessage.BuilderParent parent) {
				super(parent);
				maybeForceBuilderInitialization();
			}

			private void maybeForceBuilderInitialization() {
				if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
				}
			}

			private static Builder create() {
				return new Builder();
			}

			public Builder clear() {
				super.clear();
				name_ = "";
				bitField0_ = (bitField0_ & ~0x00000001);
				flags_ = 0;
				bitField0_ = (bitField0_ & ~0x00000002);
				return this;
			}

			public Builder clone() {
				return create().mergeFrom(buildPartial());
			}

			public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
				return ets.genielog.appwatcher_3.profiling.model.NetworkInfo.internal_static_Profiling_core_networkInfo_descriptor;
			}

			public ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo getDefaultInstanceForType() {
				return ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo
						.getDefaultInstance();
			}

			public ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo build() {
				ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo result = buildPartial();
				if (!result.isInitialized()) {
					throw newUninitializedMessageException(result);
				}
				return result;
			}

			public ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo buildPartial() {
				ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo result = new ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo(
						this);
				int from_bitField0_ = bitField0_;
				int to_bitField0_ = 0;
				if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
					to_bitField0_ |= 0x00000001;
				}
				result.name_ = name_;
				if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
					to_bitField0_ |= 0x00000002;
				}
				result.flags_ = flags_;
				result.bitField0_ = to_bitField0_;
				onBuilt();
				return result;
			}

			public Builder mergeFrom(com.google.protobuf.Message other) {
				if (other instanceof ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo) {
					return mergeFrom((ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo) other);
				} else {
					super.mergeFrom(other);
					return this;
				}
			}

			public Builder mergeFrom(
					ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo other) {
				if (other == ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo
						.getDefaultInstance())
					return this;
				if (other.hasName()) {
					bitField0_ |= 0x00000001;
					name_ = other.name_;
					onChanged();
				}
				if (other.hasFlags()) {
					setFlags(other.getFlags());
				}
				this.mergeUnknownFields(other.getUnknownFields());
				return this;
			}

			public final boolean isInitialized() {
				if (!hasName()) {

					return false;
				}
				if (!hasFlags()) {

					return false;
				}
				return true;
			}

			public Builder mergeFrom(
					com.google.protobuf.CodedInputStream input,
					com.google.protobuf.ExtensionRegistryLite extensionRegistry)
					throws java.io.IOException {
				ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo parsedMessage = null;
				try {
					parsedMessage = PARSER.parsePartialFrom(input,
							extensionRegistry);
				} catch (com.google.protobuf.InvalidProtocolBufferException e) {
					parsedMessage = (ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo) e
							.getUnfinishedMessage();
					throw e;
				} finally {
					if (parsedMessage != null) {
						mergeFrom(parsedMessage);
					}
				}
				return this;
			}

			private int bitField0_;

			// required string name = 1;
			private java.lang.Object name_ = "";

			/**
			 * <code>required string name = 1;</code>
			 * 
			 * <pre>
			 * * interface name
			 * </pre>
			 */
			public boolean hasName() {
				return ((bitField0_ & 0x00000001) == 0x00000001);
			}

			/**
			 * <code>required string name = 1;</code>
			 * 
			 * <pre>
			 * * interface name
			 * </pre>
			 */
			public java.lang.String getName() {
				java.lang.Object ref = name_;
				if (!(ref instanceof java.lang.String)) {
					java.lang.String s = ((com.google.protobuf.ByteString) ref)
							.toStringUtf8();
					name_ = s;
					return s;
				} else {
					return (java.lang.String) ref;
				}
			}

			/**
			 * <code>required string name = 1;</code>
			 * 
			 * <pre>
			 * * interface name
			 * </pre>
			 */
			public com.google.protobuf.ByteString getNameBytes() {
				java.lang.Object ref = name_;
				if (ref instanceof String) {
					com.google.protobuf.ByteString b = com.google.protobuf.ByteString
							.copyFromUtf8((java.lang.String) ref);
					name_ = b;
					return b;
				} else {
					return (com.google.protobuf.ByteString) ref;
				}
			}

			/**
			 * <code>required string name = 1;</code>
			 * 
			 * <pre>
			 * * interface name
			 * </pre>
			 */
			public Builder setName(java.lang.String value) {
				if (value == null) {
					throw new NullPointerException();
				}
				bitField0_ |= 0x00000001;
				name_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>required string name = 1;</code>
			 * 
			 * <pre>
			 * * interface name
			 * </pre>
			 */
			public Builder clearName() {
				bitField0_ = (bitField0_ & ~0x00000001);
				name_ = getDefaultInstance().getName();
				onChanged();
				return this;
			}

			/**
			 * <code>required string name = 1;</code>
			 * 
			 * <pre>
			 * * interface name
			 * </pre>
			 */
			public Builder setNameBytes(com.google.protobuf.ByteString value) {
				if (value == null) {
					throw new NullPointerException();
				}
				bitField0_ |= 0x00000001;
				name_ = value;
				onChanged();
				return this;
			}

			// required uint32 flags = 2;
			private int flags_;

			/**
			 * <code>required uint32 flags = 2;</code>
			 * 
			 * <pre>
			 * * status flag
			 * </pre>
			 */
			public boolean hasFlags() {
				return ((bitField0_ & 0x00000002) == 0x00000002);
			}

			/**
			 * <code>required uint32 flags = 2;</code>
			 * 
			 * <pre>
			 * * status flag
			 * </pre>
			 */
			public int getFlags() {
				return flags_;
			}

			/**
			 * <code>required uint32 flags = 2;</code>
			 * 
			 * <pre>
			 * * status flag
			 * </pre>
			 */
			public Builder setFlags(int value) {
				bitField0_ |= 0x00000002;
				flags_ = value;
				onChanged();
				return this;
			}

			/**
			 * <code>required uint32 flags = 2;</code>
			 * 
			 * <pre>
			 * * status flag
			 * </pre>
			 */
			public Builder clearFlags() {
				bitField0_ = (bitField0_ & ~0x00000002);
				flags_ = 0;
				onChanged();
				return this;
			}

			// @@protoc_insertion_point(builder_scope:Profiling.core.networkInfo)
		}

		static {
			defaultInstance = new networkInfo(true);
			defaultInstance.initFields();
		}

		// @@protoc_insertion_point(class_scope:Profiling.core.networkInfo)
	}

	private static com.google.protobuf.Descriptors.Descriptor internal_static_Profiling_core_networkInfo_descriptor;
	private static com.google.protobuf.GeneratedMessage.FieldAccessorTable internal_static_Profiling_core_networkInfo_fieldAccessorTable;

	public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
		return descriptor;
	}

	private static com.google.protobuf.Descriptors.FileDescriptor descriptor;
	static {
		java.lang.String[] descriptorData = { "\n\021networkInfo.proto\022\016Profiling.core\"*\n\013n"
				+ "etworkInfo\022\014\n\004name\030\001 \002(\t\022\r\n\005flags\030\002 \002(\r" };
		com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
			public com.google.protobuf.ExtensionRegistry assignDescriptors(
					com.google.protobuf.Descriptors.FileDescriptor root) {
				descriptor = root;
				internal_static_Profiling_core_networkInfo_descriptor = getDescriptor()
						.getMessageTypes().get(0);
				internal_static_Profiling_core_networkInfo_fieldAccessorTable = new com.google.protobuf.GeneratedMessage.FieldAccessorTable(
						internal_static_Profiling_core_networkInfo_descriptor,
						new java.lang.String[] { "Name", "Flags", });
				return null;
			}
		};
		com.google.protobuf.Descriptors.FileDescriptor
				.internalBuildGeneratedFileFrom(
						descriptorData,
						new com.google.protobuf.Descriptors.FileDescriptor[] {},
						assigner);
	}

	// @@protoc_insertion_point(outer_class_scope)
}
