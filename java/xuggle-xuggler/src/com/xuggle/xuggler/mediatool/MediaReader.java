/*
 * Copyright (c) 2008-2009 by Xuggle Inc. All rights reserved.
 *
 * It is REQUESTED BUT NOT REQUIRED if you use this library, that you let 
 * us know by sending e-mail to info@xuggle.com telling us briefly how you're
 * using the library and what you like or don't like about it.
 *
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.xuggle.xuggler.mediatool;

import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IError;
import com.xuggle.xuggler.video.IConverter;
import com.xuggle.xuggler.video.ConverterFactory;

/**
 * General purpose media reader.
 * <p>
 * The MediaReader class is a simplified interface to the Xuggler library
 * that opens up an {@link IContainer} object, and then every time you
 * call {@link MediaReader#readPacket()}, attempts to decode the packet and
 * call any registers {@link IMediaListener} objects for that packet.
 * </p><p>
 * The idea is to abstract away the more intricate details of the
 * Xuggler API, and let you concentrate on what you want -- the decoded
 * data.
 * </p> 
 */

public class MediaReader extends AMediaTool
{
  final private Logger log = LoggerFactory.getLogger(this.getClass());
  { log.trace("<init>"); }

  // a place to put packets
  
  protected final IPacket mPacket = IPacket.make();

  // the container of packets
  
  protected IContainer mContainer;

  // a map between stream IDs and coders

  protected Map<Integer, IStreamCoder> mCoders = 
    new HashMap<Integer, IStreamCoder>();
  
  // a map between stream IDs and resamplers

  protected Map<Integer, IVideoResampler> mVideoResamplers = 
    new HashMap<Integer, IVideoResampler>();
  
  // all the coders opened by this MediaReader which are candidates for
  // closing

  protected final Collection<IStream> mOpenedStreams = new Vector<IStream>();

  // should this media reader create buffered images

  protected final boolean mCreateBufferedImages;

  // the type of converter to use

  protected final ConverterFactory.Type mConverterType;

  // should this media reader close the container

  protected boolean mCloseContainer;

  // true if new streams can may appear during a read packet at any time

  protected boolean mStreamsCanBeAddedDynamically = false;

  //  will reader attempt to establish meta data when container opened

  protected boolean mQueryStreamMetaData = true;

  // the media url

  protected final String mUrl;

  // video picture converter

  protected IConverter mVideoConverter;

  /**
   * Create a MediaReader which reads and dispatchs data from a media
   * stream for a given source URL. The media stream is opened, and
   * subsequent calls to {@link #readPacket()} will read stream content
   * and dispatch it to attached listeners. When the end of the stream
   * is encountered the media container and it's contained streams are
   * all closed.
   * 
   * <p>
   * 
   * No {@link BufferedImage}s are created.  To create {@link
   * BufferedImage}s see {@link #MediaReader(String, boolean, String)}.
   * 
   * </p>
   *
   * @param url the location of the media content, a file name will also
   *        work here
   */

  public MediaReader(String url)
  {
    this(url, false, null);
  }

  /**
   * Create a MediaReader which reads and dispatchs data from a media
   * stream for a given source URL. The media stream is opened, and
   * subsequent calls to {@link #readPacket()} will read stream content
   * and dispatch it to attached listeners. When the end of the stream
   * is encountered the media container and it's contained streams are
   * all closed.
   *
   * @param url the location of the media content, a file name will also
   *        work here
   * @param createBufferedImages true if BufferedImages should be
   *        created during media reading
   * @param converterDescriptor the descriptive of converter to use to
   *        create buffered images; if createBufferedImages is FALSE,
   *        this may be NULL
   *
   * @throws IllegalArgumentException if BufferedImage conversion is
   *         requried and the passed converter descriptor is NULL
   * @throws UnsupportedOperationException if the converter can not be
   *         found
   */

  public MediaReader(String url, boolean createBufferedImages, 
    String converterDescriptor)
  {
    // the media url

    mUrl = url;

    // create the container

    mContainer = IContainer.make();
    
    // note that we should close the container opened here

    mCloseContainer = true;
    
    // note that we should or should not create buffered images during
    // video decoding, and if so what type of converter to use

    mCreateBufferedImages = createBufferedImages;
    if (mCreateBufferedImages)
    {
      if (converterDescriptor == null)
        throw new IllegalArgumentException(
          "If createBufferedImages is TRUE, converterDescriptor may not be NULL.");
      
      mConverterType = ConverterFactory 
        .findRegisteredConverter(converterDescriptor);
      if (mConverterType == null)
        throw new UnsupportedOperationException(
          "No converter \"" + converterDescriptor + "\" found.");
    }
    else
      mConverterType = null;
  }

  /**
   * Create a MediaReader which reads and dispatchs data from a media
   * container.  Calls to {@link #readPacket} will read stream content
   * and dispatch it to attached listeners. If the end of the media
   * stream is encountered, the MediaReader does NOT close the
   * container, that is left to the calling context (you).  Streams
   * opened by the MediaReader will be closed by the MediaReader,
   * however streams outside the MediaReader will not be closed.  In
   * short MediaReader closes what it opens.
   * 
   * <p>
   * 
   * No {@link BufferedImage}s are created.  To create {@link
   * BufferedImage}s see {@link #MediaReader(IContainer, boolean,
   * String)}.
   *
   * </p>
   *
   * @param container on already open media container
   */

  public MediaReader(IContainer container)
  {
    this(container, false, null);
  }

  /**
   * Create a MediaReader which reads and dispatchs data from a media
   * container.  Calls to {@link #readPacket} will read stream content
   * and dispatch it to attached listeners. If the end of the media
   * stream is encountered, the MediaReader does NOT close the
   * container, that is left to the calling context (you).  Streams
   * opened by the MediaReader will be closed by the MediaReader,
   * however streams outside the MediaReader will not be closed.  In
   * short MediaReader closes what it opens.
   *
   * @param container on already open media container
   * @param createBufferedImages should BufferedImages be created during
   *        media reading
   * @param converterDescriptor the descriptive of converter to use to
   *        create buffered images; if createBufferedImages is FALSE,
   *        this may be NULL
   *
   * @throws IllegalArgumentException if BufferedImage conversion is
   *         requried and the passed converter descriptor is NULL
   * @throws UnsupportedOperationException if the converter can not be
   *         found
   */

  public MediaReader(IContainer container, boolean createBufferedImages, 
    String converterDescriptor)
  {
    mUrl = container.getURL();

    // get the container

    mContainer = container;

    // get dynamic stream add ability

    mStreamsCanBeAddedDynamically = container.canStreamsBeAddedDynamically();
    
    // note that we should not close the container opened elsewere

    mCloseContainer = false;

    // note that we should or should not create buffered images during
    // video decoding, and if so what type of converter to use

    mCreateBufferedImages = createBufferedImages;
    if (mCreateBufferedImages)
    {
      if (converterDescriptor == null)
        throw new IllegalArgumentException(
          "If createBufferedImages is TRUE, converterDescriptor may not be NULL.");
      
      mConverterType = ConverterFactory 
        .findRegisteredConverter(converterDescriptor);
      if (mConverterType == null)
        throw new UnsupportedOperationException(
          "No converter \"" + converterDescriptor + "\" found.");
    }
    else
      mConverterType = null;
  }

  public IContainer getContainer()
  {
    return mContainer;
  }

  /** 
   * Set if the underlying media container supports adding dynamic
   * streams.  See {@link IContainer#open(String, IContainer.Type,
   * IContainerFormat, boolean, boolean)}.  The default value for this
   * is false.
   *
   * <p>
   * 
   * To have an effect, the MediaReader must not have been created with
   * an already open {@link IContainer}, and this method must be called
   * before the first call to {@link #readPacket}.
   *
   * </p>
   * 
   * @param streamsCanBeAddedDynamically true if new streams may appear
   *         at any time during a {@link #readPacket} call
   *
   * @throws RuntimeException if the media container is already open
   */

  public void setAddDynamicStreams(boolean streamsCanBeAddedDynamically)
  {
    if (mContainer.isOpened())
      throw new RuntimeException("media container is already open");
    mStreamsCanBeAddedDynamically = streamsCanBeAddedDynamically;
  }

  /** 
   * Report if the underlying media container supports adding dynamic
   * streams.  See {@link IContainer#open(String, IContainer.Type,
   * IContainerFormat, boolean, boolean)}.
   * 
   * @return true if new streams can may appear at any time during a
   *         {@link #readPacket} call
   */

  public boolean canAddDynamicStreams()
  {
    return mStreamsCanBeAddedDynamically;
  }

  /** 
   * Set if the underlying media container will attempt to establish all
   * meta data when the container is opened, which will potentially
   * block until it has ready enough data to find all streams in a
   * container.  If false, it will only block to read a minimal header
   * for this container format.  See {@link IContainer#open(String,
   * IContainer.Type, IContainerFormat, boolean, boolean)}.  The default
   * value for this is true.
   *
   * <p>
   * 
   * To have an effect, the MediaReader must not have been created with
   * an already open {@link IContainer}, and this method must be called
   * before the first call to {@link #readPacket}.
   *
   * </p>
   * 
   * @param queryStreamMetaData true if meta data is to be queried
   * 
   * @throws RuntimeException if the media container is already open
   */

  public void setQueryMetaData(boolean queryStreamMetaData)
  {
    if (mContainer.isOpened())
      throw new RuntimeException("media container is already open");
    
    mQueryStreamMetaData = queryStreamMetaData;
  }

  /** 
   * Report if the underlying media container will attempt to establish
   * all meta data when the container is opened, which will potentially
   * block until it has ready enough data to find all streams in a
   * container.  If false, it will only block to read a minimal header
   * for this container format.  See {@link IContainer#open(String,
   * IContainer.Type, IContainerFormat, boolean, boolean)}.
   * 
   * @return true meta data will be queried
   */

  public boolean willQueryMetaData()
  {
    return mQueryStreamMetaData;
  }

  /** Get the correct {@link IStreamCoder} for a given stream in the
   * container.  If this is a new stream not been seen before, we record
   * it and open it before returning.
   *
   * @param streamIndex the index of the stream to be added
   */

  protected IStreamCoder getStreamCoder(int streamIndex)
  {
    // if the coder does not exists, get it

    IStreamCoder coder = mCoders.get(streamIndex);
    if (coder == null)
    {
      // test valid stream index

      if (streamIndex < 0 || streamIndex >= mContainer.getNumStreams())
        throw new RuntimeException("invalid stream index");
    
      // now get the coder for the given stream index

      IStream stream = mContainer.getStream(streamIndex);
      coder = stream.getStreamCoder();
      ICodec.Type type = coder.getCodecType();

      // put the coder into the coder map, event if it not a supported
      // type so that on further reads it will find the coder but choose
      // not decode unsupported types

      mCoders.put(streamIndex, coder);

      // if the coder is not open, open it 
      // NOTE: MediaReader currently supports audio & video streams

      if (!coder.isOpen() && 
        (type==ICodec.Type.CODEC_TYPE_AUDIO || type==ICodec.Type.CODEC_TYPE_VIDEO))
      {
        if (coder.open() < 0)
          throw new RuntimeException(
            "could not open coder for stream: " + streamIndex);
        mOpenedStreams.add(stream);
        for (IMediaListener listener: getListeners())
          listener.onOpenStream(this, stream);
      }
    }
      
    // return the coder, new or cached
    
    return coder;
  }

  /**
   * This decodes the next packet and calls registered {@link IMediaListener}s.
   * 
   * <p>
   * 
   * If a complete video frame or audio sample set are decoded, it will
   * be dispatched to the listeners added to the media reader.
   *
   * </p>
   * 
   * @return null if there are more packets to read, otherwise return an
   *         IError instance.  If {@link
   *         com.xuggle.xuggler.IError#getType()} == {@link
   *         com.xuggle.xuggler.IError.Type#ERROR_EOF} then end of file
   *         has been reached.
   */
    
  public IError readPacket()
  {
    // if the container is null, report that

    if (mContainer == null)
      throw new RuntimeException("container is null");

    // if the container is not yet been opend, open it

    if (!mContainer.isOpened())
    {
      if (mContainer.open(mUrl, IContainer.Type.READ, null, 
          mStreamsCanBeAddedDynamically, mQueryStreamMetaData) < 0)
        throw new RuntimeException("could not open: " + mUrl);
      for (IMediaListener l: getListeners())
        l.onOpen(this);
    }

    // if there is an off nomial resul from read packet return the
    // correct error

    int rv = mContainer.readNextPacket(mPacket);
    if (rv < 0)
    {
      IError error = IError.make(rv);
      
      // if this is an end of file, call close
      
      if (error.getType() == IError.Type.ERROR_EOF)
      {
        close();
      }
      return error;
    }

    // inform listeners that a pacekt was read

    for (IMediaListener l: getListeners())
      l.onReadPacket(this, mPacket);

    // get the coder for this packet

    IStreamCoder coder = getStreamCoder(mPacket.getStreamIndex());

    // decode based on type

    switch (coder.getCodecType())
    {
      // decode audio

    case CODEC_TYPE_AUDIO:
      decodeAudio(coder);
      break;

      // decode video

    case CODEC_TYPE_VIDEO:
      decodeVideo(coder);
      break;

      // all other stream types are currently ignored

    default:
    }

    // return true more packets to be read

    return null;
  }
  
  /** Decode and dispatch a video packet.
   *
   * @param videoCoder the video coder
   */

  protected void decodeVideo(IStreamCoder videoCoder)
  {
    // create a blank video picture
    
    IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),
        videoCoder.getWidth(), videoCoder.getHeight());
    
    // decode the packet into the video picture
    
    int rv = videoCoder.decodeVideo(picture, mPacket, 0);
    if (rv < 0)
      throw new RuntimeException("error " + rv + " decoding video");
    
    // if this is a complete picture, dispatch the picture
    
    if (picture.isComplete())
      dispatchVideoPicture(mPacket.getStreamIndex(), picture);
  }

  /** Decode and dispatch a audio packet.
   *
   * @param audioCoder the audio coder
   */

  protected void decodeAudio(IStreamCoder audioCoder)
  {
    // if it doesn't exist, allocate a set of samples with the same
    // number of channels as in the audio coder and a stock size of 1024
    // (currently the buffer size will be expanded to 32k to conform to
    // ffmpeg)
    
    IAudioSamples samples = IAudioSamples.make(1024, audioCoder.getChannels());
    
    // packet may contain multiple audio frames, decode audio until
    // all audio frames are extracted from the packet 
    
    for (int offset = 0; offset < mPacket.getSize(); /* in loop */)
    {
      // decode audio
      
      int bytesDecoded = audioCoder.decodeAudio(samples, mPacket, offset);
      if (bytesDecoded < 0)
        throw new RuntimeException("error " + bytesDecoded + " decoding audio");
      offset += bytesDecoded;
      
      // if samples are a complete audio frame, dispatch that frame
      
      if (samples.isComplete())
        dispatchAudioSamples(mPacket.getStreamIndex(), samples);
    }
  }

  /** Dispatch a video picture to attached listeners.  This is called
   * when a complete video picture has been decoded from the packet
   * stream.  Optionally it will take the steps required to convert the
   * IVideoPicture to a BufferedImage.  If you wanted to perform a
   * custom conversion, subclass and override this method.
   *
   * @param streamIndex the index of the stream
   * @param picture the video picture to dispatch
   */


  protected void dispatchVideoPicture(int streamIndex, IVideoPicture picture)
  {
    BufferedImage image = null;
    
    // if should create buffered image, do so

    if (mCreateBufferedImages)
    {
      // if the converter is not created, create one

      if (mVideoConverter == null)
        mVideoConverter = ConverterFactory.createConverter(
          mConverterType.getDescriptor(), picture);

      // create the buffered image

      image = mVideoConverter.toImage(picture);
    }
    
    // dispatch picture here

    for (IMediaListener l: getListeners())
      l.onVideoPicture(null, picture, image, streamIndex);
  }

  /** Dispatch audio samples to attached listeners.  This is called when
   * a complete set of audio samples has been decoded from the packet
   * stream.  If you wanted to perform a common custom conversion,
   * subclass and override this method.
   *
   * @param streamIndex the index of the stream
   * @param samples the audio samples to dispatch
   */
  
  protected void dispatchAudioSamples(int streamIndex, IAudioSamples samples)
  {
    for (IMediaListener l: getListeners())
      l.onAudioSamples(null, samples, streamIndex);
  }
  
  public void close()
  {
    // close the coders opened by this

    for (IStream stream: mOpenedStreams)
    {
      stream.getStreamCoder().close();

      // inform listeners that the stream was closed

      for (IMediaListener listener: getListeners())
        listener.onCloseStream(this, stream);
    }
    // expunge all referneces to the coders and resamplers
    
    mCoders.clear();
    mOpenedStreams.clear();
    mVideoResamplers.clear();

    // if we're supposed to, close the container

    if (mCloseContainer && mContainer !=null)
    {
      mContainer.close();
      mContainer = null;
    }

    // tell the listeners that the container is closed

    for (IMediaListener l: getListeners())
      l.onClose(this);
  }
}