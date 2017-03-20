package com.redbubble.hawk.validate

import com.redbubble.hawk.{Key, KeyId}

final case class Credentials(keyId: KeyId, key: Key, algorithm: Algorithm)
