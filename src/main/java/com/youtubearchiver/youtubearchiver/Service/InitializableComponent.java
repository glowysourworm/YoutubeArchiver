package com.youtubearchiver.youtubearchiver.Service;

public interface InitializableComponent<T>
{
    void Initialize(T componentData);

    boolean IsInitialized();
}
