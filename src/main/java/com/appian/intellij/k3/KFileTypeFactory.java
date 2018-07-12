package com.appian.intellij.k3;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;

public final class KFileTypeFactory extends FileTypeFactory {

  @Override
  public void createFileTypes(FileTypeConsumer consumer) {
    consumer.consume(KFileType.INSTANCE, "k");
  }
}
